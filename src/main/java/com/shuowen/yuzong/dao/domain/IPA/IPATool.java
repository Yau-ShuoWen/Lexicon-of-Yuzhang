package com.shuowen.yuzong.dao.domain.IPA;

import com.shuowen.yuzong.Linguistics.Scheme.UniPinyin;
import com.shuowen.yuzong.dao.model.IPA.IPASyllableEntity;
import com.shuowen.yuzong.dao.model.IPA.IPAToneEntity;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class IPATool
{

    /**
     * 传入多条拼音，把所有字典版本的IPA全部转换出来
     *
     * @return 一个拼音的结果全部无效，就不会出现在结果Map里
     * @apiNote 只有两次查询，是最高效的版本
     */
    public static <T extends UniPinyin> Map<T, Map<String, String>> getMultiline(
            Set<T> p, IPAToneStyle ts, IPASyllableStyle ss,
            Set<String> dictionarySet,
            Function<Set<String>, Set<IPASyllableEntity>> syllablePvd,
            Function<Set<String>, Set<IPAToneEntity>> tonePvd)
    {
        // 结果
        Map<T, Map<String, String>> map = new HashMap<>();
        // 声母韵母集合
        Set<String> syllable = new HashSet<>(), tone = new HashSet<>();
        for (var pinyin : p)
        {
            if (pinyin.isValid())
            {
                syllable.add(pinyin.getPinyin());
                tone.add(pinyin.getTone().toString());
            }
        }

        // mapper查询，成为可以查的字典
        Map<String, IPASyllableEntity> syllableMap = new HashMap<>();
        Map<Integer, IPAToneEntity> toneMap = new HashMap<>();

        //如果查询的为空，那么会异常，接受异常后直接返回空集合
        try
        {
            for (var i : syllablePvd.apply(syllable))
                syllableMap.put(i.getStandard(), i);
            for (var i : tonePvd.apply(tone))
                toneMap.put(i.getStandard(), i);
        } catch (Exception e)
        {
            return Map.of();
        }

        // 整理结果
        for (var pinyin : p)
        {
            if (pinyin.isValid())
            {
                Map<String, String> tmp = new HashMap<>();
                boolean allNull = true;
                for (var dict : dictionarySet)
                {
                    String ans = mergeAPI(
                            Yinjie.of(syllableMap.get(pinyin.getPinyin())),
                            Shengdiao.of(toneMap.get(pinyin.getTone())),
                            pinyin, ts, ss, dict);
                    tmp.put(dict, ans == null ? UniPinyin.getError() : ans);
                    if (ans != null) allNull = false;
                }
                if (!allNull) map.put(pinyin, tmp);
            }
        }
        return map;
    }


    /**
     * 统一接口
     */
    private static <T extends UniPinyin> String mergeAPI
    (Yinjie y, Shengdiao d, T p, IPAToneStyle ts, IPASyllableStyle ss, String dict)
    {
        return formatSyllable(
                switch (ts)
                {
                    case FIVE_DEGREE_NUM -> merge(y, d, dict, true);
                    case FIVE_DEGREE_LINE -> merge(y, d, dict, false);
                    case FOUR_CORNER -> merge(y, p.getTone(true), dict);
                }, ss);
    }


    /**
     * 传入音节、声调和词典，返回 {@code saʔ˨} 形似的国际音标
     *
     * @param b true 数字 false 符号
     * @return 返回null是安全的，应该对null的数量计数如果全部都是null，说明这个拼音无效
     */
    private static String merge(Yinjie y, Shengdiao d, String dict, boolean b)
    {
        if (!y.isValid() || !d.isValid()) return null;

        String Y = y.getInfo(dict);
        String D = d.getInfo(dict);

        if ((Y + D).contains("-")) return null;

        return (b ? toFiveDegreeNum(Y, D) : toFiveDegreeLine(Y, D));
    }


    private static String toFiveDegreeNum(String syllable, String tone)
    {
        return "//" +
                syllable + (tone
                .replaceAll("[꜈꜉꜊꜋꜌0]", "⁰")// 这里是因为允许直接存特殊轻声符号，所以这里要替换回来
                .replace('1', '¹')
                .replace('2', '²')
                .replace('3', '³')
                .replace('4', '⁴')
                .replace('5', '⁵')
        ) + "//";
    }


    /**
     * 内部函数<p>
     * 自动渲染五度标记法的函数，因为有轻声标在其他地方，所以同样要传入声母
     */
    private static String toFiveDegreeLine(String syllable, String tone)
    {
        /*
         * 说实话这里作者也是百思不得其解，为什么要加这一个逻辑呢
         * 据了解，这是unicode的一个规则，因为˩˩这样的两个相同的调号，常规字体Cambria等是不能渲染好的
         * 作者还特意去找了几个特殊的字体可以渲染如Doulos SIL或者Charis SIL
         * 但是使用了这些字体还是会出现一个问题：
         * - 当他后面什么字符都没有的时候，˩˩显示是正常的，宽度却仍然是一个字符的宽度
         * - 当他后面有任何字符的时候，˩˩会重新分裂成两个字符，所以作者就很崩溃、
         * - 一般市面上的就睁只眼闭只眼过去了，就连维基百科也不例外，但为了美观还是得改
         * - 找到一个取巧的办法，就是换成三次重复˩˩˩，实现了宽度为两个字符
         *
         * 这个hack是在Word上测试稳定通过的
         * 参考链接 作者本人的号：https://chatgpt.com/c/68381c08-540c-8005-a331-6f7626887868
         *         外部访问：https://chatgpt.com/share/683828dd-1a64-8005-bea2-3443e3a88a35
         * 日期2025/05/29
         *
         * TODO:前端处理的时候要注意如果要给复制还要替换回来
         * */
        if (tone.length() == 2 && tone.charAt(0) == tone.charAt(1))
        {
            tone = tone + tone.charAt(1);
        }


        if (tone.equals("0")) return "//·" + syllable + "//";
        else return "//" + syllable + "//--" + (tone
                .replace('1', '˩')
                .replace('2', '˨')
                .replace('3', '˧')
                .replace('4', '˦')
                .replace('5', '˥'))
                + "--";
    }


    // true：在左边，false在右边
    private static final Map<Character, Boolean> leftOrRight = Map.of(
            '꜀', true,
            '꜁', true,
            '꜂', true,
            '꜃', true,
            '꜄', false,
            '꜅', false,
            '꜆', false,
            '꜇', false
    );

    /**
     * 传入音节，四角类声调和词典，返回 {@code ꜁tsɨn} 形似的国际音标(好像显示不了)
     *
     * @param D 调用 {@code XxxPinyin.getTone(true)} 得到的结果
     */
    private static String merge(Yinjie y, Character D, String dict)
    {
        if (!y.isValid()) return null;

        String Y = y.getInfo(dict);

        if (Y.contains("-")) return null;
        return (leftOrRight.get(D) ?
                "--" + D + "--" + "//" + Y + "//" :
                "//" + Y + "//" + "--" + D + "--");
    }


    /**
     * 有9个音标和1个送气符号在汉语言学界之中通用，但却未能被国际音标接受。
     */
    private static String formatSyllable(String s, IPASyllableStyle ss)
    {
        return switch (ss)
        {
            case CHINESE_SPECIAL -> s;
            case STANDARD_IPA -> s.
                    replace("'", "ʰ").
                    replace("ɿ", "ɹ̩").
                    replace("ʅ", "ɻ̍").
                    replace("ʮ", "ɹ̩ʷ").
                    replace("ʯ", "ɻ̍ʷ").
                    replace("ȶ", "t̠ʲ").
                    replace("ȡ", "d̠ʲ").
                    replace("ȵ", "ṉʲ").
                    replace("ᴀ", "ä").
                    replace("ᴇ", "e̞");
        };
    }


    /**
     * @return 返回 {@code Yinjie.of(null)} 是因为已经用null传参了就会被认为音节无效，是空安全的
     */
    public static Yinjie constructIPA(UniPinyin pinyin, Function<Pair<String, String>, Pair<YinjiePart, YinjiePart>> dataPvd)
    {
        if (!pinyin.isValid()) return Yinjie.of(null);

        String code = pinyin.getCode();

        int sl = pinyin.shengmuLength();
        int l = code.length();
        if (l <= sl) throw new IllegalArgumentException("一般还到不了这里，你这拼音配置有问题啊");


        String shengmu = code.substring(0, sl) + "~".repeat(l - sl); //05~~~
        String yunmu = "~".repeat(sl) + code.substring(sl);                //~~123

        var data = dataPvd.apply(Pair.of(shengmu, yunmu));

        return Yinjie.of(data.getLeft(), data.getRight());
    }

    public static Pair<Map<String, Integer>, Set<String>> check(
            Supplier<List<IPASyllableEntity>> syllablePvd,
            Supplier<List<IPASyllableEntity>> elementPvd,
            Function<String, UniPinyin> pinyinFctr
    )
    {
        int success = 0, fail = 0;

        Set<String> failCase = new HashSet<>();

        Set<Yinjie> a = new HashSet<>();
        Map<String, YinjiePart> b = new HashMap<>();

        for (var i : syllablePvd.get()) a.add(Yinjie.of(i));
        for (var i : elementPvd.get())
            b.put(YinjiePart.of(i).getCode(), YinjiePart.of(i));

        for (var i : a)
        {
            var merge = constructIPA(pinyinFctr.apply(i.getPinyin()),
                    (Pair<String, String> code) ->
                            Pair.of(b.get(code.getLeft()), b.get(code.getRight())));

            if (i.equals(merge)) success++;
            else
            {
                fail++;
                failCase.add(i.getPinyin());
                System.out.println(merge);
                System.out.println(i);
                System.out.println();
            }
        }
        var ans = Pair.of(Map.of("success", success, "fail", fail), failCase);
        System.out.println(ans);
        return ans;
    }

    public static void updateIPA(
            Supplier<List<IPASyllableEntity>> syllablePvd,
            Supplier<List<IPASyllableEntity>> elementPvd,
            Function<String, UniPinyin> pinyinFctr,
            Consumer<IPASyllableEntity> updateCsm
    )
    {
        Set<Yinjie> a = new HashSet<>();
        Map<String, YinjiePart> b = new HashMap<>();
        for (var i : syllablePvd.get()) a.add(Yinjie.of(i));
        for (var i : elementPvd.get())
            b.put(YinjiePart.of(i).getCode(), YinjiePart.of(i));

        for (var i : a)
        {
            var merge = constructIPA(
                    pinyinFctr.apply(i.getPinyin()),
                    (Pair<String, String> code) ->
                            Pair.of(b.get(code.getLeft()), b.get(code.getRight())));

            if (!i.equals(merge))
                updateCsm.accept(merge.transfer());
        }
    }

    public static <T extends UniPinyin> void insertSyllable(
            T p,
            Function<String, IPASyllableEntity> elementPvd,
            Consumer<IPASyllableEntity> insertConsumer)
    {
        var merge = constructIPA(p,
                (Pair<String, String> code) ->
                        Pair.of(YinjiePart.of(elementPvd.apply(code.getLeft())),
                                YinjiePart.of(elementPvd.apply(code.getRight()))));

        insertConsumer.accept(merge.transfer());
    }
}
