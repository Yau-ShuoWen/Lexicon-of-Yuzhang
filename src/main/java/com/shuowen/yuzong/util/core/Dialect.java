package com.shuowen.yuzong.util.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.shuowen.yuzong.dict.data.domain.Reference.DictCode;
import com.shuowen.yuzong.linguistics.pinyin.CEDPinyin;
import com.shuowen.yuzong.linguistics.pinyin.LACPinyin;
import com.shuowen.yuzong.linguistics.pinyin.UniPinyin;
import com.shuowen.yuzong.linguistics.pinyin.WUHPinyin;
import com.shuowen.yuzong.linguistics.util.KeyboardPinyin;
import com.shuowen.yuzong.linguistics.util.SplitedPinyin;
import com.shuowen.yuzong.util.err.InvalidPinyinException;
import com.shuowen.yuzong.util.text.ScTcText;
import com.shuowen.yuzong.util.tuple.Maybe;
import lombok.Getter;

import java.util.List;
import java.util.function.Function;

/**
 * 方言代码
 */
@SuppressWarnings ({"unchecked", "unused"})
public enum Dialect
{
    LAC("南昌話", "lac", LACPinyin::tryOf, LACPinyin::tryOf, "ncdict", 2),
    CED("成都話", "ced", CEDPinyin::tryOf, CEDPinyin::tryOf, "cddict", 2),
    WUH("武漢話", "wuh", WUHPinyin::tryOf, WUHPinyin::tryOf, "whdict", 2),
    //JIN("濟南話", "jin", null, null, null, "jndict", 0),
    //HGZ("濟南話", "hgz", null, null, null, "hzdict", 0),
    ;

    @Getter
    private final ScTcText name;
    private final String code;
    private final Function<SplitedPinyin, Maybe<? extends UniPinyin>> creatorFromSplitedPinyin;
    private final Function<KeyboardPinyin, Maybe<? extends UniPinyin>> creatorFromKeyboardPinyin;
    @Getter
    private final DictCode defaultDict;

    /**
     * 返回{@code code}里从头开始多少位是声母编码，剩下的就是介韵母的编码长度了<p>
     * 这里名称只是因为声母的英文 {@code initial} 的写法，和初始化无关
     */
    @Getter
    private final int initialLength;

    /**
     * 构造函数
     */
    <T extends UniPinyin>
    Dialect(String name, String code,
            Function<SplitedPinyin, Maybe<? extends UniPinyin>> creatorFromSplitedPinyin,
            Function<KeyboardPinyin, Maybe<? extends UniPinyin>> creatorFromKeyboardPinyin,
            String defaultDict, int initialLength
    )
    {
        this.name = ScTcText.forEnum(name);
        this.code = code;
        this.creatorFromSplitedPinyin = creatorFromSplitedPinyin;
        this.creatorFromKeyboardPinyin = creatorFromKeyboardPinyin;
        this.defaultDict = new DictCode(defaultDict);
        this.initialLength = initialLength;
    }

    @JsonCreator
    public static Dialect of(String s)
    {
        for (Dialect d : values())
        {
            if (d.code.equalsIgnoreCase(s)) return d;
        }
        throw new IllegalArgumentException("方言代号无效：" + s);
    }

    @JsonValue
    @Override
    public String toString()
    {
        return code;
    }

    public <T extends UniPinyin> Maybe<T> tryCreatePinyin(KeyboardPinyin py)
    {
        return (Maybe<T>) creatorFromKeyboardPinyin.apply(py);
    }

    public <T extends UniPinyin> Maybe<T> tryCreatePinyin(SplitedPinyin py)
    {
        return (Maybe<T>) creatorFromSplitedPinyin.apply(py);
    }

    /**
     * 当从流程本身就能保证这个字符串是有效的拼音的时候（从数据库里拿出来的数据），使用这个
     *
     * @return 直接返回内容，不需要解析，但是如果是无效的，就直接报异常，说明流程的漏洞把缺陷的拼音存进去了
     */
    public <T extends UniPinyin> T trustedCreatePinyin(SplitedPinyin py)
    {
        var pinyin = creatorFromSplitedPinyin.apply(py);
        if (pinyin.isEmpty()) throw new IllegalArgumentException("来自信任端的拼音无效。文本：" + py);
        return (T) pinyin.getValue();
    }

    public <T extends UniPinyin> T trustedCreatePinyin(String py)
    {
        return trustedCreatePinyin(SplitedPinyin.of(py));
    }

    /**
     * 用于前端传送到后端的拼音检查，如果通过检查，返回拼音，如果没有通过，报错
     *
     * @throws InvalidPinyinException 碰见这个错误不用打印栈
     */
    public <T extends UniPinyin> T checkAndCreatePinyin(KeyboardPinyin py)
    {
        var pinyin = tryCreatePinyin(py);
        if (pinyin.isEmpty()) throw new InvalidPinyinException(String.format("%s拼音%s无效", getName().getSc(), py));
        return (T) pinyin.getValue();
    }

    public <T extends UniPinyin> T checkAndCreatePinyin(String py)
    {
        return checkAndCreatePinyin(KeyboardPinyin.of(py));
    }

    public static List<Dialect> getList()
    {
        return List.of(values());
    }
}
