package com.shuowen.yuzong.linguistics.pinyinv2.lac;

import com.shuowen.yuzong.dict.data.domain.IPA.IPASyllStyle;
import com.shuowen.yuzong.dict.data.domain.IPA.IPAToneStyle;
import com.shuowen.yuzong.dict.data.domain.IPA.PinyinMode;
import com.shuowen.yuzong.linguistics.pinyinv2.DictCode;
import com.shuowen.yuzong.linguistics.pinyinv2.UniPinyin;
import com.shuowen.yuzong.linguistics.util.*;
import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.err.InvalidPinyinException;
import com.shuowen.yuzong.util.ext.other.ObjectTool;
import com.shuowen.yuzong.util.text.StringRef;
import com.shuowen.yuzong.util.text.StringTool;
import com.shuowen.yuzong.util.tuple.Either;
import com.shuowen.yuzong.util.tuple.Maybe;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.Map;
import java.util.function.Function;

public class LACPinyin extends UniPinyin
{
    @AllArgsConstructor
    protected enum ShengMu
    {
        $("", "00"),
        b("b", "01"),
        p("p", "02"),
        m("m", "03"),
        f("f", "04"),
        d("d", "05"),
        t("t", "06"),
        l("l", "07"),
        g("g", "08"),
        k("k", "09"),
        ŋ("ŋ", "10"),
        h("h", "11"),
        j("j", "12"),
        q("q", "13"),
        n("n", "14"),
        x("x", "15"),
        z("z", "16"),
        c("c", "17"),
        s("s", "18");

        final String name;
        final String code;

        public static ShengMu of(StringRef pinyin)
        {
            String s = pinyin.s;
            for (var i : values())
            {
                if (i == $) continue;

                if (s.startsWith(i.name))
                {
                    if (s.length() <= 1) throw new InvalidPinyinException("仅包含声母");//长度小于1，并且这个是声母
                    pinyin.s = s.substring(1);
                    return i;
                }
            }
            return $;
        }
    }

    @AllArgsConstructor
    protected enum JieMu
    {
        $("", "0"),
        i("i", "1"),
        u("u", "2"),
        ü("ü", "3");

        final String name;
        final String code;

        public static JieMu of(StringRef pinyin)
        {
            String s = pinyin.s;

            for (var i : values())
            {
                if (i == $) continue;

                if (s.startsWith(i.name))
                {
                    pinyin.s = s.substring(1);
                    return i;
                }
            }
            return $;
        }
    }

    protected enum YunMu
    {
        $("", "0"),
        ı("ı", "1"),
        a("a", "2"),
        o("o", "3"),
        e("e", "4"),
        ẹ("ẹ", "5"),
        ọ("ọ", "6"),
        u("u", "7"),
        ;

        final String name;
        final String code;

        YunMu(String name, String code)
        {
            this.name = name;
            this.code = code;
        }

        public static YunMu of(StringRef pinyin)
        {
            for (var i : values())
            {
                if (pinyin.s.equals(i.name)) return i;
            }
            throw new InvalidPinyinException("");
        }
    }

    @AllArgsConstructor
    protected enum YunWei
    {
        $("", "0"),
        i("i", "1"),
        u("u", "2"),
        n("n", "3"),
        ŋ("ŋ", "4"),
        t("t", "5"),
        k("k", "6"),
        l("l", "7"),
        ;

        final String name;
        final String code;

        public static YunWei of(StringRef pinyin)
        {
            for (var i : YunWei.values())
            {
                if (i == $) continue;

                if (pinyin.s.endsWith(i.name))
                {
                    pinyin.s = pinyin.s.substring(0, pinyin.s.length() - 1);
                    return i;
                }
            }
            return $;
        }
    }

    @AllArgsConstructor
    protected enum YinDiao
    {
        $(-1, "", ""),
        t0(0, "", ""),
        t1(1, "̀", "↘"),
        t2(2, "́", "↗"),
        t3(3, "̌", "↘↗"),
        t4(4, "̄", "→"),
        t5(5, "̉", "↓"),
        t6(6, "̋", "↑↑"),
        t7(7, "̏", "↓↓");

        final Integer code;
        @Getter
        final String mark;
        @Getter
        final String arrow;

        public static YinDiao of(Maybe<Integer> tone)
        {
            if (tone.isEmpty()) return $;

            for (var i : YinDiao.values())
            {
                if (i == $) continue;
                if (i.code == tone.getValue().intValue()) return i;
            }
            return null;
        }

        public Maybe<Integer> getCode()
        {
            if (this == $) return Maybe.nothing();
            else return Maybe.exist(this.code);
        }

        public String getWeight()
        {
            if (this == $) return "__";
            else return "0" + this.code;
        }
    }

    @AllArgsConstructor
    protected enum BianDiao
    {
        t33(33, "́", "↗"),
        t55(55, "́", "↗"),
        t77(77, "́", "↗");

        final Integer code;
        @Getter
        final String mark;
        @Getter
        final String arrow;

        public static BianDiao of(Integer tone)
        {
            for (var i : BianDiao.values())
            {
                if (i.code == tone.intValue()) return i;
            }
            return null;
        }

        public Maybe<Integer> getCode()
        {
            return Maybe.exist(this.code);
        }

        public String getWeight()
        {
            return this.code.toString();
        }
    }

    @AllArgsConstructor
    protected enum DanDu
    {
        m("m", "1"),
        n("n", "2"),
        ŋ("ŋ", "3");

        final String name;
        final String code;

        public static DanDu of(String pinyin)
        {
            for (var i : DanDu.values())
                if (i.name.equals(pinyin)) return i;
            return null;
        }

        public String toString()
        {
            return name;
        }

        public String getWeight()
        {
            return code + "00000";
        }
    }

    @Data
    protected static class YinJie
    {
        private final ShengMu shengmu;
        private final JieMu jiemu;
        private final YunMu yunmu;
        private final YunWei yunwei;

        // 顺序韵尾和韵母反过来
        private YinJie(ShengMu shengmu, JieMu jiemu, YunWei yunwei, YunMu yunmu)
        {
            this.shengmu = shengmu;
            this.jiemu = jiemu;
            this.yunmu = yunmu;
            this.yunwei = yunwei;
        }

        public String toString()
        {
            // 自然顺序 b i a ng
            return shengmu.name + jiemu.name + yunmu.name + yunwei.name;
        }

        public String getYun()
        {
            return jiemu.name + yunmu.name + yunwei.name;
        }

        public String getWeight()
        {
            // 排序顺序 声母 韵尾 介母 韵母
            return "0" + shengmu.code + yunwei.code + jiemu.code + yunmu.code;
        }
    }

    protected final Either<DanDu, YinJie> yinjieOrDandu;
    protected final Either<YinDiao, BianDiao> yindiao;

    public static Maybe<LACPinyin> tryOf(SplitedPinyin p)
    {
        try
        {
            return Maybe.exist(new LACPinyin(p));
        } catch (InvalidPinyinException e)
        {
            return Maybe.nothing();
        }
    }

    public static Maybe<LACPinyin> tryOf(KeyboardPinyin p)
    {
        return tryOf(LACKeyboard.normalize(p));
    }

    protected LACPinyin(SplitedPinyin s)
    {
        try
        {
            StringRef syll = new StringRef(s.getSyll());
            var tone = s.getTone();

            yinjieOrDandu = Either.firstNonNull(
                    () -> DanDu.of(syll.s),
                    () -> new YinJie(ShengMu.of(syll), JieMu.of(syll), YunWei.of(syll), YunMu.of(syll))
            ).getValueDirectly("拼音无效");

            // 如果音调超出范围，得到的结果是一个Either<null,null> → Maybe.noting() 故意强行解包抛出异常
            yindiao = Either.firstNonNull(() -> YinDiao.of(tone), () -> BianDiao.of(tone.getValue())).getValueDirectly("音调超出范围");

        } catch (Exception e)
        {
            throw new InvalidPinyinException("拼音编码出现异常");
        }
    }

    @Override
    public String getSyll()
    {
        return yinjieOrDandu.fold(DanDu::toString, YinJie::toString);
    }

    @Override
    public Maybe<Integer> getTone()
    {
        return yindiao.fold(YinDiao::getCode, BianDiao::getCode);
    }

    private String getMark()
    {
        return yindiao.fold(YinDiao::getMark, BianDiao::getMark);
    }

    private String getArrow()
    {
        return yindiao.fold(YinDiao::getArrow, BianDiao::getArrow);
    }

    @Override
    public String getWeight()
    {
        return yinjieOrDandu.fold(DanDu::getWeight, YinJie::getWeight) +
                yindiao.fold(YinDiao::getWeight, BianDiao::getWeight);
    }

    @Override
    public Dialect getDialect()
    {
        return Dialect.LAC;
    }

    @Override
    public String toString()
    {
        return String.format("南昌话拼音：%s%s", getSyll(), getTone().toStringOrEmpty());
    }

    @Override
    public Maybe<String> searchIPA()
    {
        return LACIPA.query(this);
    }

    @Override
    public Maybe<String> searchIPA(DictCode dict)
    {
        return LACIPA.query(this, dict);
    }

    @Override
    public Maybe<String> searchIPA(DictCode dict, IPASyllStyle syllStyle, IPAToneStyle toneStyle)
    {
        return LACIPA.query(this, dict, syllStyle, toneStyle);
    }

    @Override
    public PinyinBlock format(PinyinMode md)
    {
        PinyinBlock block = new PinyinBlock();

        Function<String, String> fun = s -> String.format("[%s]", PinyinCommon.e_A_G(s));

        String display = fun.apply(LACDisplay.format(this));
        String keyboard = fun.apply(LACKeyboard.format(this));
        String introduce = fun.apply(LACIntro.format(this));

        switch (md)
        {
            case INTRODUCE ->
            {
                block.setTitle(introduce);
                block.add("就像普通話的", introduce);
                block.add("標準寫法", display);
                block.add("鍵盤輸入", keyboard);
            }
            case STANDARD, PROFESSIONAL ->
            {
                block.setTitle(display);
                block.add("印刷和書寫", display);
                block.add("鍵盤輸入", keyboard);
            }
        }
        return block;
    }

    @Override
    public RPinyin toRPinyin()
    {
        return RPinyin.of(PinyinCommon.e_A_G(LACPinyin.LACDisplay.format(this)));
    }

    @Override
    public KeyboardPinyin toKeyboardPinyin()
    {
        return KeyboardPinyin.of(LACPinyin.LACKeyboard.format(this));
    }

    @Override
    public DatabasePinyin toDatabasePinyin()
    {
        return DatabasePinyin.of(getSyll() + getToneStr());
    }


    /**
     * 展示格式工具类，单向
     */
    private static class LACDisplay
    {
        public static String format(LACPinyin p)
        {
            String s = p.getSyll();

            s = PinyinCommon.d_ZCSR(s);
            s = PinyinCommon.d_Ng(s);

            String t = p.getMark();

            if (s.contains("iu")) return s.replace("u", "u" + t);

            for (String i : "aoọeẹiuü".split(""))
                if (s.contains(i)) return s.replace(i, i + t);

            // 例外：没有主元音m n ng，只有ng要特殊处理
            if ("ng".equals(s)) return StringTool.insert(s, 1, t);
            else return s + t;
        }
    }

    /**
     * 输入格式工具类，双向
     */
    private static class LACKeyboard
    {
        public static String format(LACPinyin p)
        {
            String s = p.getSyll();

            s = s.replace("ü", "yu");
            s = s.replace("ẹ", "ee");
            s = s.replace("ọ", "oe");
            s = PinyinCommon.d_ZCSR(s);
            s = PinyinCommon.d_Ng(s);

            return s + p.getToneStr();
        }

        private final static Map<Character, String> tones = Map.of(
                '̀', "1",
                '́', "2",
                '̌', "3",
                '̄', "4",
                '̉', "5",
                '̋', "6",
                '̏', "7"
        );

        public static SplitedPinyin normalize(KeyboardPinyin p)
        {
            if (p.getTone().isEmpty())
                p = ToneParser.parse(p.getSyll(), tones);

            String s = p.getSyll().toLowerCase();

            // 标准替换
            s = s.replace("ee", "ẹ");
            s = s.replace("oe", "ọ");
            s = PinyinCommon.e_ZCSR(s);
            s = PinyinCommon.e_Ng(s);

            // i u ü 问题
            s = PinyinCommon.e_Yi(s);
            s = PinyinCommon.e_Wu(s);
            s = PinyinCommon.e_JQX_Ü_V_Yu_U(s);
            s = PinyinCommon.e_Ü_V_Yu(s);

            // r->l 问题
            s = s.replace("r", "l");

            // 双韵母的模糊处理
            // 匹配：普通话常见但是不符合的： ao->au  iau->ieu  ou->eu  iou->iu uei->ui
            if (s.contains("ao")) s = s.replace("ao", "au");
            if (s.contains("iau")) s = s.replace("iau", "ieu");
            if (s.contains("ou"))
            {
                if (s.contains("iou"))
                    s = s.replace("iou", "iu");
                else s = s.replace("ou", "eu");
            }
            if (s.contains("uei")) s = s.replace("uei", "ui");

            // 鼻韵母的模糊处理
            // 匹配：普通话常见但是不符合的： ian->ien  üan->üon  uen->un
            s = s.replaceAll("ian$", "ien"); // 用$防止iang
            s = s.replace("üan", "üon");
            s = s.replace("uen", "un");

            var t = p.getTone();

            if (!t.isEmpty())
            {
                t = switch (t.getValue())
                {
                    case 0, 1, 2, 3, 4, 5, 6, 7, 33, 55, 77 -> Maybe.exist(t.getValue());
                    default -> throw new InvalidPinyinException("");
                };
            }

            return SplitedPinyin.of(s, t);
        }
    }

    /**
     * 简化拼音
     */
    private static class LACIntro
    {
        public static String format(LACPinyin p)
        {
            String s = p.getSyll();

            s = s.replace("ien", "ian").replace("üon", "üan");
            s = s.replaceAll("[tk]$", ""); // 删除入声韵尾
            s = handleYW(s);
            s = PinyinCommon.d_Yu_display(s);
            s = s.replace("ẹ", "e");
            s = s.replace("ọ", "o");
            s = PinyinCommon.d_ZCSR(s);
            s = PinyinCommon.d_Ng(s);

            // 标音调
            String t = p.getMark();
            String a = p.getArrow();

            if (s.contains("iu")) return s.replace("u", "u" + t) + a;

            for (String i : "aoọeẹiuü".split(""))
                if (s.contains(i)) return s.replace(i, i + t) + a;

            // 例外：没有主元音m n ng，只有ng要特殊处理
            if ("ng".equals(s)) return StringTool.insert(s, 1, t) + a;
            else return s + t + a;
        }

        /**
         * 合理添加yw，使得看起来更符合普通话规律
         */
        private static String handleYW(String s)
        {
            char c = s.charAt(0);
            if (c == 'i')
            {
                // i ->yi it->yit iu->yiu in->yin
                if (ObjectTool.existEqual(s, "i", "it", "in"))
                    s = "y" + s;
                else if (s.equals("iu")) s = "yiu";
                else s = "y" + s.substring(1);
            }
            if (c == 'u')
            {
                if (s.length() >= 2 && ObjectTool.existEqual(s.charAt(1), 'a', 'o'))
                    s = "w" + s.substring(1);
                else if (s.equals("ui")) s = "wi";
                else s = "w" + s;
            }
            return s;
        }
    }
}
