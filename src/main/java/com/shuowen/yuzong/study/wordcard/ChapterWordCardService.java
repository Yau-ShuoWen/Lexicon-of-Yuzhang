package com.shuowen.yuzong.study.wordcard;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shuowen.yuzong.study.curriculum.StudyChapterMapper;
import com.shuowen.yuzong.study.curriculum.StudyPartMapper;
import com.shuowen.yuzong.study.curriculum.data.StudyChapterEntity;
import com.shuowen.yuzong.study.curriculum.data.StudyPartEntity;
import com.shuowen.yuzong.study.wordcard.data.ChapterWordCardAssignment;
import com.shuowen.yuzong.study.wordcard.data.ChapterWordCardEntity;
import com.shuowen.yuzong.study.wordcard.data.ChapterWordCardGroup;
import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.json.JsonTool;
import com.shuowen.yuzong.util.text.ScTcText;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class ChapterWordCardService
{
    private final StudyPartMapper partMapper;
    private final StudyChapterMapper chapterMapper;
    private final WordCardMapper wordCardMapper;
    private final ChapterWordCardMapper relationMapper;
    private final WordCardEditService wordCardEditService;

    public ChapterWordCardService(StudyPartMapper partMapper, StudyChapterMapper chapterMapper,
                                  WordCardMapper wordCardMapper, ChapterWordCardMapper relationMapper,
                                  WordCardEditService wordCardEditService)
    {
        this.partMapper = partMapper;
        this.chapterMapper = chapterMapper;
        this.wordCardMapper = wordCardMapper;
        this.relationMapper = relationMapper;
        this.wordCardEditService = wordCardEditService;
    }

    public List<ChapterWordCardGroup> getBoard(Dialect dialect)
    {
        List<WordCardEntity> cards = wordCardMapper.findAll(dialect.toString());
        Map<Integer, WordCardUpdate> cardsById = new HashMap<>();
        cards.forEach(card -> cardsById.put(card.id, new WordCardUpdate(card, dialect)));

        List<ChapterWordCardEntity> relations = relationMapper.findAllByDialect(dialect.toString());
        if (relations.size() != cards.size())
            throw new IllegalStateException("存在未归入章节的词卡，请先执行词卡归档迁移 SQL");

        Map<Integer, List<WordCardUpdate>> cardsByChapter = new HashMap<>();
        for (ChapterWordCardEntity relation : relations)
        {
            WordCardUpdate card = cardsById.get(relation.getWordCardId());
            if (card == null) throw new IllegalStateException("词卡章节关系包含无效数据");
            cardsByChapter.computeIfAbsent(relation.getChapterId(), key -> new ArrayList<>()).add(card);
        }

        List<ChapterWordCardGroup> result = new ArrayList<>();
        for (StudyPartEntity part : partMapper.findAll(dialect.toString()))
        {
            for (StudyChapterEntity chapter : chapterMapper.findByPartId(part.getId()))
            {
                result.add(new ChapterWordCardGroup(
                        part.getId(), part.getPartOrder(), text(part.getTitle()),
                        chapter.getId(), chapter.getChapterOrder(), text(chapter.getTitle()),
                        cardsByChapter.getOrDefault(chapter.getId(), List.of())
                ));
            }
        }
        return result;
    }

    public List<ChapterWordCardGroup> saveBoard(List<ChapterWordCardAssignment> assignments, Dialect dialect)
    {
        if (assignments == null) throw new IllegalArgumentException("词卡编排不能为空");
        List<ChapterWordCardGroup> board = getBoard(dialect);
        Set<Integer> validChapterIds = new HashSet<>();
        board.forEach(group -> validChapterIds.add(group.getChapterId()));

        Set<Integer> submittedChapterIds = new HashSet<>();
        List<Integer> submittedCardIds = new ArrayList<>();
        for (ChapterWordCardAssignment assignment : assignments)
        {
            if (assignment == null || assignment.getChapterId() == null || assignment.getWordCardIds() == null)
                throw new IllegalArgumentException("词卡编排内容不完整");
            if (!submittedChapterIds.add(assignment.getChapterId()))
                throw new IllegalArgumentException("章节不能重复");
            submittedCardIds.addAll(assignment.getWordCardIds());
        }
        if (!submittedChapterIds.equals(validChapterIds))
            throw new IllegalArgumentException("章节结构已变化，请刷新后重试");

        Set<Integer> uniqueCardIds = new HashSet<>(submittedCardIds);
        Set<Integer> validCardIds = new HashSet<>();
        wordCardMapper.findAll(dialect.toString()).forEach(card -> validCardIds.add(card.id));
        if (uniqueCardIds.size() != submittedCardIds.size() || !uniqueCardIds.equals(validCardIds))
            throw new IllegalArgumentException("每张词卡必须且只能属于一个章节");

        relationMapper.deleteAllByDialect(dialect.toString());
        for (ChapterWordCardAssignment assignment : assignments)
        {
            for (int index = 0; index < assignment.getWordCardIds().size(); index++)
            {
                ChapterWordCardEntity relation = new ChapterWordCardEntity();
                relation.setChapterId(assignment.getChapterId());
                relation.setWordCardId(assignment.getWordCardIds().get(index));
                relation.setCardOrder(index + 1);
                relationMapper.insert(relation);
            }
        }
        return getBoard(dialect);
    }

    public WordCardUpdate createCard(Integer chapterId, WordCardUpdate update, Dialect dialect)
    {
        boolean validChapter = getBoard(dialect).stream().anyMatch(group -> group.getChapterId().equals(chapterId));
        if (!validChapter) throw new IllegalArgumentException("章节不存在");
        update.setId(null);
        WordCardUpdate saved = wordCardEditService.save(update, dialect);
        int nextOrder = relationMapper.findAllByDialect(dialect.toString()).stream()
                .filter(item -> item.getChapterId().equals(chapterId))
                .mapToInt(ChapterWordCardEntity::getCardOrder).max().orElse(0) + 1;
        ChapterWordCardEntity relation = new ChapterWordCardEntity();
        relation.setChapterId(chapterId);
        relation.setWordCardId(saved.getId());
        relation.setCardOrder(nextOrder);
        relationMapper.insert(relation);
        return saved;
    }

    public WordCardUpdate editCard(WordCardUpdate update, Dialect dialect)
    {
        if (update == null || update.getId() == null || update.getId() <= 0)
            throw new IllegalArgumentException("词卡编号无效");
        return wordCardEditService.save(update, dialect);
    }

    public void deleteCard(Integer cardId, Dialect dialect)
    {
        if (wordCardMapper.findById(cardId, dialect.toString()) == null)
            throw new IllegalArgumentException("词卡不存在");
        wordCardEditService.delete(cardId, dialect);
    }

    private ScTcText text(String json)
    {
        return JsonTool.readJson(json, new TypeReference<>() {});
    }
}
