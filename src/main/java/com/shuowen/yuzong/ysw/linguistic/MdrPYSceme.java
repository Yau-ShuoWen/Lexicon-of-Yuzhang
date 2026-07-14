package com.shuowen.yuzong.ysw.linguistic;

import com.shuowen.yuzong.Linguistics.Mandarin.Zhuyin;
import com.shuowen.yuzong.Linguistics.Scheme.PinyinCommon;
import com.shuowen.yuzong.Linguistics.Scheme.RPinyin;
import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import com.shuowen.yuzong.util.err.InvalidPinyinException;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

/**
 * 基于替换注音符号的历史拼音方案
 */
public class MdrPYSceme
{
    private static class Phoneme
    {
        final Map<Zhuyin, String> rule = new LinkedHashMap<>();

        public Phoneme(String s)
        {
            if (s.isEmpty()) return;

            String[] writing = s.split(" ");

            for (String i : writing)
            {
                var arr = i.split("->");
                rule.put(Zhuyin.of(arr[0]), arr[1]);
            }
        }

        public Zhuyin preHandle(Zhuyin zy)
        {
            for (var i : rule.keySet())
            {
                // 两个除了声调部分都一样。
                if (Objects.equals(zy.toStringWithoutTone(), i.toStringWithoutTone()))
                {
                    zy = Zhuyin.of(rule.get(i) + zy.getTheTone());
                }
            }
            return zy;
        }
    }

    private static class Single
    {
        final Map<String, String> rule = new LinkedHashMap<>();

        private static final String[] order = "ㄓ ㄔ ㄕ ㄖ ㄗ ㄘ ㄙ".split(" ");

        public Single(String s)
        {
            String[] writing = s.split(" ");

            for (int i = 0; i < Math.min(writing.length, order.length); i++)
                rule.put(order[i], writing[i]);
            printInfo(order, writing);
        }

        /**
         * 为空的情况就是这并不是一个舌音整体认读音节
         */
        public Maybe<String> get(Zhuyin zy)
        {
            return Maybe.uncertain(rule.get(zy.toStringWithoutTone()));
        }
    }

    private static class Initial
    {
        final Map<String, String> rule = new LinkedHashMap<>();

        private static final String[] order = "ㄅ ㄆ ㄇ ㄈ ㄉ ㄊ ㄋ ㄌ ㄍ ㄎ ㄏ ㄐ ㄑ ㄒ ㄓ ㄔ ㄕ ㄖ ㄗ ㄘ ㄙ".split(" ");

        public Initial(String s)
        {
            String[] writing = s.split(" ");
            //if (writing.length != order.length)

            for (int i = 0; i < Math.min(writing.length, order.length); i++)
                rule.put(order[i], writing[i]);
            printInfo(order, writing);
        }

        /**
         * 为空的情况就是这是一个零声母的音节
         */
        public Maybe<String> get(Zhuyin zy)
        {
            return Maybe.uncertain(rule.get(zy.getInitial()));
        }
    }

    private static class Last
    {
        final Map<Pair<String, Boolean>, String> rule = new LinkedHashMap<>();

        private static final String[] order = "ㄚ ㄛ ㄜ ㄝ ㄞ ㄟ ㄠ ㄡ ㄢ ㄣ ㄤ ㄥ ㄦ ㄧ ㄧㄚ ㄧㄛ ㄧㄝ ㄧㄠ ㄧㄡ ㄧㄢ ㄧㄣ ㄧㄤ ㄧㄥ ㄨ ㄨㄚ ㄨㄛ ㄨㄞ ㄨㄟ ㄨㄢ ㄨㄣ ㄨㄤ ㄨㄥ ㄩ ㄩㄝ ㄩㄢ ㄩㄣ ㄩㄥ".split(" ");

        public Last(String s)
        {
            String[] writing = s.split(" ");
            //if (writing.length != order.length)

            for (int i = 0; i < Math.min(writing.length, order.length); i++)
            {
                String w = writing[i];

                if (w.contains("/"))
                {
                    var arr = w.split("/");
                    rule.put(Pair.of(order[i], true), arr[0]);
                    rule.put(Pair.of(order[i], false), arr[1]);
                }
                else
                {
                    rule.put(Pair.of(order[i], true), writing[i]);
                    rule.put(Pair.of(order[i], false), writing[i]);
                }
            }
            printInfo(order, writing);
        }

        public String get(Zhuyin zy, boolean zero)
        {
            return Maybe.exist(rule.get(Pair.of(
                    zy.getMiddle() + zy.getLast(), zero))).getValue();
        }
    }

    private static class Tone
    {
        final BiFunction<Integer, String, String> fun;

        public Tone(BiFunction<Integer, String, String> fun)
        {
            if (fun == null) this.fun = (tone, s) -> s; // 不加音调
            else this.fun = fun;
        }

        public String format(Zhuyin zy, String s)
        {
            return fun.apply(zy.getTheTone(), s);
        }
    }

    private static void printInfo(String[] order, String[] writing)
    {
        System.out.println();
        System.out.println("\n==================== INFO ====================");
        System.out.println("Expected : " + order.length);
        System.out.println("Actual   : " + writing.length);
        System.out.println();

        int max = Math.max(order.length, writing.length);

        for (int i = 0; i < max; i++)
        {
            String left =
                    i < order.length
                            ? order[i]
                            : "<missing-order>";

            String right =
                    i < writing.length
                            ? writing[i]
                            : "<missing-writing>";

            String mark =
                    Objects.equals(left, right)
                            ? " "
                            : "*";

            System.out.printf(
                    "%2d | %-10s -> %-15s %s%n",
                    i,
                    left,
                    right,
                    mark
            );
        }

        System.out.println("=================== ====================");
        System.out.println();
    }

    private final Phoneme phoneme;
    private final Single single;
    private final Initial initial;
    private final Last last;
    private final Tone tone;

    private MdrPYSceme(Alphabet a, String single, String initial, String last, String phoneme, BiFunction<Integer, String, String> tone)
    {
        System.out.printf("\n=============== START: %s ===============%n", a.getCode());

        this.single = new Single(single);
        this.initial = new Initial(initial);
        this.last = new Last(last);
        this.phoneme = new Phoneme(phoneme);
        this.tone = new Tone(tone);

        System.out.printf("\n================= END =================%n");
    }

    private static final Map<Alphabet, MdrPYSceme> CACHE = new ConcurrentHashMap<>();

    public static MdrPYSceme of(Alphabet d)
    {
        return switch (d)
        {
            case Wade, ZhuyinII, TYPinyin -> CACHE.get(d);
            default -> throw new InvalidPinyinException("");
        };
    }

    public RPinyin format(Zhuyin zy)
    {
        zy = phoneme.preHandle(zy);

        String pinyin = "";

        // 先处理整体认读
        var ans = single.get(zy);
        if (!ans.isEmpty())
        {
            pinyin = ans.getValue();
        }
        else
        {
            ans = initial.get(zy);
            pinyin += ans.getValueOrDefault("");
            pinyin += last.get(zy, pinyin.isEmpty());
        }

        pinyin = tone.format(zy, pinyin);

        return RPinyin.of(pinyin);
    }

    // 初始化
    static
    {
        CACHE.put(Alphabet.ZhuyinII, new MdrPYSceme(
                Alphabet.ZhuyinII,
                "jr chr shr r tz tsz sz",
                "b p m f d t n l g k h j ch sh j ch sh r tz ts s",
                "a o e e ai ei au ou an en ang eng er yi/i ya/ia yo/io ye/ie yau/iao you/iou yan/ian yin/in yang/iang ying/ing wu/u wa/ua wo/uo wai/uai wei/uei wan/uan wen/uen wang/uang weng/ung yu/iu yue/iue yuan/iuan yun/iun yung/iung",
                "",
                (tone, syll) ->
                {
                    Map<String, String[]> map = Map.of(
                            "a", "a ā á ǎ à".split(" "),
                            "o", "o ō ó ǒ ò".split(" "),
                            "e", "e ē é ě è".split(" "),
                            "i", "i ī í ǐ ì".split(" "),
                            "u", "u ū ú ǔ ù".split(" "),
                            "z", "z z̄ ź ž z̀".split(" "),
                            "r", "r r̄ ŕ ř r̀".split(" ")
                    );
                    for (String i : "aoeiuzr".split(""))
                        if (syll.contains(i)) return syll.replace(i, map.get(i)[tone]);
                    return syll;
                }
        ));

        CACHE.put(Alphabet.TYPinyin, new MdrPYSceme(
                Alphabet.TYPinyin,
                "jhih chih shih rih zih cih sih",
                "b p m f d t n l g k h j c s jh ch sh r z c s",
                "a o e e ai ei au ou an en ang eng er yi/i ya/ia yo/io ye/ie yao/iao you/iou yan/ian yin/in yang/iang ying/ing wu/u wa/ua wo/uo wai/uai wei/uei wan/uan wun/un wang/uang wong/ong yu yue yuan yun yong",
                "",
                (tone, syll) ->
                {
                    Map<String, String[]> map = Map.of(
                            "a", "å ā á ǎ à".split(" "),
                            "o", "o̊ ō ó ǒ ò".split(" "),
                            "e", "e̊ ē é ě è".split(" "),
                            "i", "i̊ ī í ǐ ì".split(" "),
                            "u", "ů ū ú ǔ ù".split(" ")
                    );
                    for (String i : "aoeiuü".split(""))
                        if (syll.contains(i)) return syll.replace(i, map.get(i)[tone]);
                    return syll;
                }
        ));

        CACHE.put(Alphabet.Wade, new MdrPYSceme(
                Alphabet.Wade,
                "chih chʻih shih jih tzŭ tzʻŭ ssŭ",
                "p pʻ m f t tʻ n l k kʻ h ch chʻ hs ch chʻ sh j ts tsʻ s",
                "a o ê eh ai ei ao ou an ên ang êng êrh i ya/ia yo/io yeh/ieh yao/iao yu/iu yen/ien yin/in yang/iang ying/ing wu/u wa/ua wo/uo wai/uai wei/ui wan/uan wen/un wang/uang wêng/ung yü/ü yüeh/üeh yüan/üan yün/ün yung/iung",
                "ge->go ke->ko he->ho duo->do tuo->to nuo->no luo->lo zhuo->zho chuo->cho shuo->sho ruo->ro zuo->zo cuo->co suo->so",
                (tone, syll) -> syll + PinyinCommon.toSuperScript(tone)
        ));

        CACHE.put(Alphabet.HanYale,new MdrPYSceme(
                Alphabet.HanYale,
                "jr chr shr r dz tsz sz",
                "b p m f d t n l g k h jy chy sy j ch sh r dz ts s",
                "a wo e e ai ei au ou an en ang eng ya ye yau you yan yin yang ying wa wo wai wei wan wen wang weng/ung yu ywe ywan yun yung",
                "",
                null
        ));


        CACHE.put(Alphabet.HanRussia, new MdrPYSceme(
                Alphabet.HanRussia,
                "чжи чи ши жи цзы цы сы",
                "б п м ф д т н л г к х цз ц с чж ч ш ж цз ц с",
                "а о э э ай эй ао оу ань энь ан эн эр и я - е яо ю янь инь ян ин у уа/ва о/во уай/вай уй/вэй уань/вань унь/вэнь уан/ван ун/вэн юй юэ юань юнь юн",
                "",
                null
        ));

//        CACHE.put(Alphabet.HanRussiaNew, new MdrPYSceme(
//                Alphabet.HanRussiaNew,
//                "жи чи ши ри цзы цы зы",
//                "б п м ф д т н л г к х цз ц с ж ч ш р цз ц з",
//                "а о э э ай эй аy оу ань энь ан эн эр и я - е яу ю янь инь ян ин у уа/ва о/во уай/вай уй/вэй уань/вань унь/вэнь уан/ван ун/вэн юй юэ юань юнь юн",
//                "",
//                null
//        ));
//
//        CACHE.put(Alphabet.HanUkraine, new MdrPYSceme(
//                Alphabet.HanUkraine,
//                "",
//                "б п м ф д т н л ґ к х цз ц с чж ч ш ж цз ц с",
//                "？？а о э э ай эй ао оу ань энь ан эн эр и я - е яо ю янь инь ян ин у уа/ва о/во уай/вай уй/вэй уань/вань унь/вэнь уан/ван ун/вэн юй юэ юань юнь юн",
//                "",
//                null
//        ));
//
//        CACHE.put(Alphabet.HanBelarus, new MdrPYSceme(
//                Alphabet.HanBelarus,
//                "чжы чы шы жы цзы цы сы",
//                "б п м ф д т н л г к х цз ц с чж ч ш ж цз ц с",
//                "а о э э ай эй аа оу ань энь ан эн эр і я - е яа ю янь інь ян ін у уа/ва о/во уай/вай уй/вэй уань/вань унь/вэнь уан/ван ун/вэн юй юэ юань юнь юн",
//                "",
//                null
//        ));
//
//        CACHE.put(Alphabet.HanSerbia, new MdrPYSceme(
//                Alphabet.HanSerbia,
//                "џи чи ши жи ци ци си",
//                "б п м ф д т н л г к х ђ ћ с џ ч ш ж ц ц с",
//                "",
//                "",
//                null
//        ));
//
//        CACHE.put(Alphabet.HanMavrfonis, new MdrPYSceme(
//                Alphabet.HanMavrfonis,
//                "",
//                "",
//                "",
//                "",
//                null
//        ));
//
//        CACHE.put(Alphabet.HanBulgaria, new MdrPYSceme(
//                Alphabet.HanBulgaria,
//                "",
//                "",
//                "",
//                "",
//                null
//        ));
    }

    // 测试
    public static void main(String[] args)
    {
        MdrPYSceme.of(Alphabet.Wade);
    }
}
