package com.shuowen.yuzong.dict.hanzi.domain;

import com.shuowen.yuzong.dict.data.domain.Pinyin.PinyinConfig;
import com.shuowen.yuzong.dict.data.domain.Reference.RefItem;
import com.shuowen.yuzong.dict.hanzi.model.MdrChar;
import com.shuowen.yuzong.dict.service.Reference.RefReadService;
import com.shuowen.yuzong.linguistics.util.RPinyin;
import com.shuowen.yuzong.linguistics.pinyin.UniPinyin;
import com.shuowen.yuzong.util.ext.list.ListTool;
import com.shuowen.yuzong.util.text.RichTextUtil;
import com.shuowen.yuzong.util.text.UChar;
import com.shuowen.yuzong.util.text.UString;
import com.shuowen.yuzong.util.tuple.Maybe;
import com.shuowen.yuzong.util.tuple.Twin;
import lombok.Data;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
public class HanziShow
{
    private final UChar hanzi;
    private final Integer special;
    private final LinkedHashSet<UChar> similar;
    private final List<Pronunciation> pinyin = new ArrayList<>();
    private final List<Twin<UString>> note;
    private final LinkedHashSet<RefItem> ref = new LinkedHashSet<>();

    @Data
    public static class Pronunciation
    {
        private RPinyin pinyin;
        private UString tag;
        private List<MandarinPronunciation> mandarin;
        private final List<Word> words = new ArrayList<>();
    }

    @Data
    public static class Word
    {
        private final Integer id;
        private final UString word;
        private final com.shuowen.yuzong.linguistics.util.RPinyins pinyin;

        public static Word of(HanziWordUsage usage)
        {
            return new Word(usage.getWordId(), usage.getWord(), usage.getPinyin());
        }
    }

    @Data
    public static class MandarinPronunciation
    {
        private final String pinyin;
        private final String zhuyin;
    }

    public static HanziShow of(HanziGroup group, PinyinConfig config, List<MdrChar> candidates,
                               List<HanziWordUsage> wordUsages)
    {
        return new HanziShow(group.getData(), config, candidates, wordUsages);
    }

    private HanziShow(List<HanziItem> items, PinyinConfig config, List<MdrChar> candidates,
                      List<HanziWordUsage> wordUsages)
    {
        var first = items.get(0);
        hanzi = first.getHanzi();
        special = first.getSpecial();
        similar = new LinkedHashSet<>();
        for (var item : items) similar.addAll(item.getSimilar());

        note = ListTool.mapping(first.getNote(), pair -> Twin.of(pair.getLeft(),
                RichTextUtil.format(pair.getRight(), config, false, Maybe.nothing(), true)));

        var dialect = config.getDialect();
        Map<Integer, MdrChar> candidateMap = candidates.stream()
                .collect(Collectors.toMap(MdrChar::getMandarinId, Function.identity()));

        for (var item : items)
        {
            boolean automatic = item.getPinyin().size() == 1 && candidates.size() == 1;
            for (var source : item.getPinyin())
            {
                var value = new Pronunciation();
                value.pinyin = dialect.trustedCreatePinyin(source.getPinyin().toString()).toRPinyin();
                value.tag = source.getTag().get(config.getLanguage());

                List<Integer> ids = automatic && source.getMandarin().isEmpty()
                        ? List.of(candidates.get(0).getMandarinId()) : source.getMandarin();

                // 普通話表中簡繁字形是不同記錄。先按當前頁面字形篩選，再按實際讀音去重。
                Map<String, MandarinPronunciation> uniqueMandarin = new LinkedHashMap<>();
                ids.stream().map(candidateMap::get).filter(i -> i != null)
                        .filter(i -> MdrTool.getHanzi(i.getInfo()).equals(hanzi.toString()))
                        .forEach(i -> uniqueMandarin.putIfAbsent(
                                MdrTool.getPinyinKey(i.getInfo()),
                                new MandarinPronunciation(
                                        MdrTool.showWithPinyin(i.getInfo()),
                                        MdrTool.showWithZhuyin(i.getInfo()))));
                value.mandarin = new ArrayList<>(uniqueMandarin.values());

                Set<Integer> addedWordIds = new HashSet<>();
                UniPinyin hanziPinyin = dialect.trustedCreatePinyin(source.getPinyin().toString());
                wordUsages.stream()
                        .filter(usage -> hanziPinyin.matches(usage.getCharacterPinyin()))
                        .filter(usage -> addedWordIds.add(usage.getWordId()))
                        .map(Word::of)
                        .forEach(value.words::add);
                pinyin.add(value);
            }
        }
        ref.addAll(RefReadService.getRef(first.getHanzis().toText(), config));
    }
}
