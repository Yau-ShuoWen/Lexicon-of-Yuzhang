package com.shuowen.yuzong.linguistics.pinyinv2;

import com.shuowen.yuzong.dict.data.domain.IPA.IPASyllStyle;
import com.shuowen.yuzong.dict.data.domain.IPA.IPAToneStyle;
import com.shuowen.yuzong.dict.data.domain.IPA.PinyinMode;
import com.shuowen.yuzong.linguistics.util.DatabasePinyin;
import com.shuowen.yuzong.linguistics.util.KeyboardPinyin;
import com.shuowen.yuzong.linguistics.util.PinyinBlock;
import com.shuowen.yuzong.linguistics.util.RPinyin;
import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.tuple.Maybe;

import java.util.Objects;

abstract public class UniPinyin
{
    abstract public String getSyll();

    abstract public Maybe<Integer> getTone();

    protected String getToneStr()
    {
        return getTone().toStringOrEmpty();
    }

    abstract public String getWeight();

    abstract public Dialect getDialect();

    /**
     * 子类必须也要重写得非常「难看」，目的是仅用于调试，不用于输出。
     */
    @Override
    public String toString()
    {
        return "（未知类型）方言拼音：" + getSyll() + getToneStr();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (!(obj instanceof UniPinyin other)) return false;
        return Objects.equals(getSyll(), other.getSyll()) &&
                Objects.equals(getTone(), other.getTone());
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(getSyll(), getTone());
    }

    public boolean matches(UniPinyin other)
    {
        return other != null && equals(other);
    }

    public abstract PinyinBlock format(PinyinMode md);

    public abstract RPinyin toRPinyin();

    public abstract KeyboardPinyin toKeyboardPinyin();

    public abstract DatabasePinyin toDatabasePinyin();

    public abstract Maybe<String> searchIPA();

    public abstract Maybe<String> searchIPA(DictCode dict);

    public abstract Maybe<String> searchIPA(DictCode dict, IPASyllStyle syllStyle, IPAToneStyle toneStyle);

}
