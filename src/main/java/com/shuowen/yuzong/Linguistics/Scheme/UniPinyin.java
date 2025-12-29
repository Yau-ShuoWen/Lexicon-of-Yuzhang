package com.shuowen.yuzong.Linguistics.Scheme;

import com.shuowen.yuzong.Linguistics.Format.PinyinStyle;
import com.shuowen.yuzong.data.domain.Pinyin.PinyinChecker;
import lombok.Getter;

import java.util.Comparator;
import java.util.Objects;

abstract public class UniPinyin<T extends PinyinStyle> implements Pinyin
{
    protected String pinyin;       // 不包括声调的标准拼音
    protected Integer tone;        // 数字音调，0表示轻声和没有声调
    protected String code = null;  // 拼音编码
    protected String show = "";    // toString时候的承载体

    /**
     * 不可以直接读取上面的字段：1. 前两个就算有，拼音也不一定是有效的
     * <ul>
     *     <li>pinyin 和 tone 就算有内容，整个拼音也不一定是有效的</li>
     *     <li>code 为 null 的时候是没有初始化，为"无效"时拼音无效</li>
     *     <li>show 只能通过 toString 来获得</li>
     * </ul>
     */
    @Getter
    protected boolean valid;

    protected static final String INVALID = "无效拼音";

    public String getPinyin()
    {
        return isValid() ? pinyin : INVALID;
    }

    public Integer getTone()
    {
        return isValid() ? tone : -1;
    }

    public String getCode()
    {
        if (isValid())
        {
            if (code == null) toCode();// 对于数据库里获得的数据，创建的时候没有检查code，现在现生成
            return code;
        }
        else return INVALID;
    }

    /**
     * 音节使用的是code，音调使用的是tone
     */
    public Integer getWeight()
    {
        return isValid() ? Integer.parseInt(getCode() + tone) : -1;
    }

    public static String getError()
    {
        return INVALID;
    }

    /**
     * 返回code里从头开始多少位是声母编码
     *
     * @apiNote 剩下的就是介韵母的编码长度了
     */
    public abstract int syllableLen();

    /**
     * 在其他内容一样的时候，只比较 pinyin 和 tone
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        return (pinyin.equals(((UniPinyin<?>) obj).pinyin)) && (tone.equals(((UniPinyin<?>) obj).tone));
    }

    @Override
    public int hashCode()
    {
        return pinyin.hashCode() + tone.hashCode();
    }

    /**
     * @param trusty true 说明是可信任的来源，比如数据库，检查就少了。false就要经过严格的检查，有任何错误都是无效
     */
    protected UniPinyin(String s, boolean trusty)
    {
        var tmp = PinyinChecker.trySplit(s);  // 空字符串的检查杂这个函数里
        pinyin = tmp.getLeft();
        tone = tmp.getRight();

        // 如果拼音来源不可信任，检查有效性
        // 1. 编码的过程是否顺利？ toCode()函数，如果中途发生字符串等错误，无效，直接返回
        // 2. 获得的编码是否可逆？ encodeable()函数，如果不可逆，无效，直接返回
        // 3. 音调是否符合范围？和韵尾的搭配是否合理？ isToneValid()函数
        if (!trusty)
        {
            if (!toCode() || !encodable() || !toneValid())
            {
                valid = false;
                return;
            }
        }
        valid = true;
    }

    /**
     * 返回四角调类
     */
    public abstract char getFourCornerTone();


    /**
     * 判断这个拼音编码和反编码是否是可逆的，不一定有效，但是可以防止{@code oiiai} {@code iuiui} {@code buia}等绝对乱码
     *
     * @implNote 在前面如果成功生成了{@code code}，尝试是否可以反推回拼音，使得：结构不对的部分虽然可能转换为code，但倒推结果一定不一样
     */
    protected boolean encodable()
    {
        return Objects.equals(constuctPinyin(), pinyin);
    }


    /**
     * 子类必须重写
     */
    @Override
    public String toString()
    {
        return (valid) ? "默认的未知方言拼音：" + pinyin + tone : INVALID;
    }

    /**
     * 带上复杂的个性化参数，根据子类而定
     */
    abstract public String toString(T params);


    /**
     * 将一个音节字符串转码为一个字符串，便于音素结构的分析与处理。
     */
    protected abstract boolean toCode();


    /**
     * {@code toCode()}函数的逆运算，尝试倒推
     *
     * @apiNote 不可以在保证code可计算的情况之外使用，可能导致null异常
     */
    protected abstract String constuctPinyin();


    /**
     * 检查音调是否合理。
     */
    protected abstract boolean toneValid();

    public static final Comparator<UniPinyin<?>> ASC = Comparator.comparingInt(UniPinyin::getWeight);

    public static final Comparator<UniPinyin<?>> DESC = ASC.reversed();
}
