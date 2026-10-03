package com.shuowen.yuzong.study.level;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shuowen.yuzong.study.curriculum.StudyLevelMapper;
import com.shuowen.yuzong.study.curriculum.data.StudyLevelEntity;
import com.shuowen.yuzong.study.streak.StreakShowService;
import com.shuowen.yuzong.study.wordcard.WordCardPractice;
import com.shuowen.yuzong.study.wordcard.WordCardService;
import com.shuowen.yuzong.user.data.model.UserEntity;
import com.shuowen.yuzong.user.service.UserService;
import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.core.Language;
import com.shuowen.yuzong.util.json.JsonTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 单一固定关卡的最小运行流程。
 *
 * <p>当前只生成配对题，答案和报告仍由前端处理；后端负责创建一次尝试、校验尝试归属，
 * 并在完成时统一结算连胜。</p>
 */
@Service
public class StudyLevelService
{
    private static final String QUESTION_TYPE = "match_pair";
    private static final int QUESTION_COUNT = 3;
    private static final int PAIR_COUNT = 5;
    private static final Duration ATTEMPT_TTL = Duration.ofMinutes(30);

    private final Map<String, StudyLevelAttempt> attempts = new ConcurrentHashMap<>();

    @Autowired
    private WordCardService wordCardService;

    @Autowired
    private StudyLevelMapper studyLevelMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private StreakShowService streakShowService;

    public StudyLevelStartResponse start(String token, Language language, Dialect dialect)
    {
        UserEntity user = userService.getUserByToken(token);
        cleanupExpiredAttempts();

        List<StudyLevelQuestion> questions = new java.util.ArrayList<>();
        for (int i = 1; i <= QUESTION_COUNT; i++)
        {
            List<WordCardPractice> cards = wordCardService.getRandomCards(dialect, language);
            if (cards.size() != PAIR_COUNT)
            {
                throw new IllegalArgumentException("学习词卡数量不足，暂时无法组成完整题目");
            }
            questions.add(new StudyLevelQuestion(i, QUESTION_TYPE, cards));
        }

        String attemptId = UUID.randomUUID().toString();
        StudyLevelAttempt attempt = new StudyLevelAttempt(
                attemptId, user.getId(), language, dialect, questions, LocalDateTime.now());
        attempts.put(attemptId, attempt);
        return new StudyLevelStartResponse(attemptId, QUESTION_TYPE, questions);
    }

    /**
     * 根据已发布的关卡配置开始学习。
     *
     * <p>旧的无 levelId 接口保留给当前演示流程；新目录应使用此接口。</p>
     */
    public StudyLevelStartResponse start(String token, Language language, Dialect dialect, Integer levelId)
    {
        if (levelId == null || levelId <= 0) throw new IllegalArgumentException("关卡编号无效");

        UserEntity user = userService.getUserByToken(token);
        cleanupExpiredAttempts();

        StudyLevelEntity level = studyLevelMapper.findById(levelId, dialect.toString());
        if (level == null || !Integer.valueOf(1).equals(level.getStatus()))
            throw new IllegalArgumentException("关卡不存在或尚未发布");
        if (!QUESTION_TYPE.equals(level.getQuestionType()))
            throw new IllegalArgumentException("当前关卡题型暂不支持");

        int questionCount = level.getQuestionCount() == null ? QUESTION_COUNT : level.getQuestionCount();
        int pairCount = level.getPairCount() == null ? PAIR_COUNT : level.getPairCount();
        if (questionCount <= 0 || pairCount < 2)
            throw new IllegalArgumentException("关卡配置无效");

        List<Integer> cardIds = JsonTool.readJson(level.getCardIds(), new TypeReference<>() {});
        List<WordCardPractice> configuredCards = wordCardService.getCardsByIds(dialect, language, cardIds);
        if (!configuredCards.isEmpty() && configuredCards.size() != pairCount)
            throw new IllegalArgumentException("关卡指定的词卡数量不足");

        List<StudyLevelQuestion> questions = new java.util.ArrayList<>();
        for (int i = 1; i <= questionCount; i++)
        {
            List<WordCardPractice> cards = configuredCards.isEmpty()
                    ? wordCardService.getRandomCards(dialect, language, pairCount)
                    : configuredCards;
            if (cards.size() != pairCount)
                throw new IllegalArgumentException("学习词卡数量不足，暂时无法组成完整题目");
            questions.add(new StudyLevelQuestion(i, QUESTION_TYPE, cards));
        }

        String attemptId = UUID.randomUUID().toString();
        StudyLevelAttempt attempt = new StudyLevelAttempt(
                attemptId, user.getId(), language, dialect, questions, LocalDateTime.now());
        attempts.put(attemptId, attempt);
        return new StudyLevelStartResponse(attemptId, QUESTION_TYPE, questions);
    }

    @Transactional(rollbackFor = Exception.class)
    public StudyLevelFinishResponse finish(String token, String attemptId)
    {
        if (attemptId == null || attemptId.isBlank())
        {
            throw new IllegalArgumentException("关卡尝试编号不能为空");
        }

        UserEntity user = userService.getUserByToken(token);
        StudyLevelAttempt attempt = attempts.get(attemptId);
        if (attempt == null)
        {
            throw new IllegalArgumentException("关卡尝试不存在或已经过期");
        }
        if (!user.getId().equals(attempt.getUserId()))
        {
            throw new IllegalArgumentException("无权完成这次关卡尝试");
        }
        if (isExpired(attempt))
        {
            attempts.remove(attemptId, attempt);
            throw new IllegalArgumentException("关卡尝试已经过期，请重新开始");
        }

        synchronized (attempt)
        {
            if (attempt.isCompleted())
            {
                int questionCount = attempt.getQuestions().size();
                return new StudyLevelFinishResponse(attemptId, true, true, questionCount, questionCount);
            }

            // 当前版本由前端确认配对完成；后端只接受一次合法的 finish，并负责正式结算。
            streakShowService.markStudyCompleted(token);
            attempt.markCompleted();
            int questionCount = attempt.getQuestions().size();
            return new StudyLevelFinishResponse(attemptId, true, true, questionCount, questionCount);
        }
    }

    private void cleanupExpiredAttempts()
    {
        attempts.entrySet().removeIf(entry -> isExpired(entry.getValue()));
    }

    private boolean isExpired(StudyLevelAttempt attempt)
    {
        return attempt.getCreatedAt().plus(ATTEMPT_TTL).isBefore(LocalDateTime.now());
    }
}
