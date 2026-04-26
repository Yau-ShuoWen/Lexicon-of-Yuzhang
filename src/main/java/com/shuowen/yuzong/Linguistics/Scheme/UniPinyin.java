package com.shuowen.yuzong.Linguistics.Scheme;

import com.shuowen.yuzong.Linguistics.Format.PinyinStyle;
import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import com.shuowen.yuzong.Tool.dataStructure.error.InvalidPinyinException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode // 因为其他字段只由这 syll tone 两个决定，所以实际上也只是比较这两个的区别
abstract public class UniPinyin<T extends PinyinStyle> implements Pinyin
{
    protected final String syll;        // 不包括声调的标准拼音
    protected final Maybe<Integer> tone;// 数字音调，0表示轻声

    protected final String code;        // 拼音编码
    protected final Integer corner;     // 四角调类的数字序号
    protected final String weight;      // 排序用的权重

    public Boolean haveTone()
    {
        return tone.isValid();
    }

    public Integer getToneDirectly()
    {
        return tone.getValue();
    }

    /**
     * 私有的构造函数，子类构造函数也应该是私有的
     *
     * @throws InvalidPinyinException 在子类静态工厂里捕获这个错误，说明构造错误，拼音建构失败，返回空集合
     */
    protected UniPinyin(SPinyin s)
    {
        syll = s.getSyll();
        tone = s.getTone();

        code = initCode(); // 1. 编码的过程是否顺利？ initCode()，如果正常，把结果赋值code，否则抛出异常
        checkEncodable();  // 2. 获得的编码是否可逆？ encodeable()，不可逆在函数里会抛出异常
        checkToneValid();  // 3. 音调的范围和搭配是否合理？ isToneValid()函数，不合理在函数里会抛出异常

        corner = initCorner();
        weight = initWeight();
    }

    // 下面六个函数会且仅会在构造函数里用到一次

    protected abstract String initCode();

    protected abstract void checkToneValid();

    protected abstract void checkEncodable();

    protected abstract int initCorner();

    protected abstract String initWeight();

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
    abstract protected RPinyin toRPinyin(T params);

    abstract protected SPinyin toSPinyin(T params);

    abstract protected DPinyin toDPinyin(T params);
}
