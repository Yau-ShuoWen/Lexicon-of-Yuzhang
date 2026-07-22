package com.shuowen.yuzong.Linguistics.pinyin;

import com.shuowen.yuzong.Linguistics.Format.PinyinStyle;
import com.shuowen.yuzong.Linguistics.IPA.IPinyin;
import com.shuowen.yuzong.Linguistics.Scheme.DPinyin;
import com.shuowen.yuzong.Linguistics.Scheme.RPinyin;
import com.shuowen.yuzong.Linguistics.Scheme.SPinyin;
import com.shuowen.yuzong.util.tuple.Maybe;
import com.shuowen.yuzong.util.err.InvalidPinyinException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode // 因为其他字段只由这 syll tone 两个决定，所以实际上也只是比较这两个的区别
abstract public class UniPinyin<T extends PinyinStyle> implements IPinyin
{
    protected final String syll;        // 不包括声调的标准拼音
    protected final Maybe<Integer> tone;// 数字音调，0表示轻声

    protected final String code;        // 拼音编码
    protected final Integer corner;     // 四角调类的数字序号
    protected final String weight;      // 排序用的权重

    /**
     * 私有的构造函数，子类构造函数也应该是私有的
     *
     * @throws InvalidPinyinException 在子类静态工厂里捕获这个错误，说明构造错误，拼音建构失败，返回空集合
     */
    protected UniPinyin(SPinyin s)
    {
        syll = s.getSyll();
        tone = initTone(s.getTone());

        code = initCode(); // 1. 编码的过程是否顺利？ initCode()，如果正常，把结果赋值code，否则抛出异常
        checkEncodable();  // 2. 获得的编码是否可逆？ encodeable()，不可逆在函数里会抛出异常
        checkToneValid();  // 3. 音调的范围和搭配是否合理？ isToneValid()函数，不合理在函数里会抛出异常

        corner = initCorner();
        weight = initWeight();
    }

    // 下面六个函数会且仅会在构造函数里用到一次

    protected Maybe<Integer> initTone(Maybe<String> t)
    {
        if (t.isEmpty()) return Maybe.nothing();
        else
        {
            try
            {
                return Maybe.exist(Integer.parseInt(t.getValue()));
            } catch (NumberFormatException e)
            {
                throw new InvalidPinyinException(String.format("%s不是一个正确的声调", t.getValue()));
            }
        }
    }

    public abstract String initCode();

    public abstract void checkToneValid();

    public abstract void checkEncodable();

    public abstract int initCorner();

    public abstract String initWeight();

    // 转字符串

    /**
     * 子类必须也要重写得非常「难看」，目的是仅用于调试，不用于输出。
     */
    @Override
    public String toString()
    {
        return "默认的未知方言拼音：" + syll + (tone.isValid() ? tone.getValue() : "");
    }

    /**
     * 这才是真正的转字符串的函数，但是
     * <br>1. 是protected的，也就是只給{@code PinyinFormatter}
     * <br>2. 结果并不是简单的字符串，而是包装类
     */
    public abstract RPinyin toRPinyin(T params);

    public abstract SPinyin toSPinyin(T params);

    public abstract DPinyin toDPinyin(T params);
}
