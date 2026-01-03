package com.shuowen.yuzong.Linguistics.Scheme;

import com.shuowen.yuzong.Linguistics.Format.PinyinStyle;
import com.shuowen.yuzong.Tool.dataStructure.error.InvalidPinyinException;
import com.shuowen.yuzong.data.domain.Pinyin.PinyinChecker;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode // 因为其他字段只由这 pinyin tone 两个决定，所以实际上也只是比较这两个的区别
abstract public class UniPinyin<T extends PinyinStyle> implements Pinyin
{
    protected final String pinyin;      // 不包括声调的标准拼音
    protected final Integer tone;       // 数字音调，0表示轻声或没有声调

    protected final String code;        // 拼音编码
    protected final Character mark;     // 音调字符
    protected final Integer corner;     // 四角调类的数字序号
    protected final String weight;      // 排序用的权重

    /**
     * 私有的构造函数，子类构造函数也应该是私有的
     *
     * @throws InvalidPinyinException 在子类静态工厂里捕获这个错误，说明构造错误，拼音建构失败，返回空集合
     */
    protected UniPinyin(String s)
    {
        var tmp = PinyinChecker.trySplit(s); // 空字符串的检查已经在这个函数里
        pinyin = tmp.getLeft();
        tone = tmp.getRight();

        code = initCode(); // 1. 编码的过程是否顺利？ initCode()，如果正常，把结果赋值code，否则抛出异常
        checkEncodable();  // 2. 获得的编码是否可逆？ encodeable()，不可逆在函数里会抛出异常
        checkToneValid();  // 3. 音调的范围和搭配是否合理？ isToneValid()函数，不合理在函数里会抛出异常

        mark = initMark();
        corner = initCorner();
        weight = initWeight();
    }

    // 下面六个函数会且仅会在构造函数里用到一次

    protected abstract String initCode();

    protected abstract void checkToneValid();

    protected abstract void checkEncodable();

    protected abstract char initMark();

    protected abstract int initCorner();

    protected abstract String initWeight();


    // 下面这一个函数返回的是固定值

    /**
     * 返回{@code code}里从头开始多少位是声母编码，剩下的就是介韵母的编码长度了<p>
     * 这里名称只是因为声母的英文 {@code initial} 的写法，和初始化无关
     */
    public abstract int getInitialLen();


    // 转字符串

    /**
     * 子类必须也要重写得非常「难看」，使得只在真正要展示的时候能立刻发现没有加参数的情况
     */
    @Override
    public String toString()
    {
        return "默认的未知方言拼音：" + pinyin + tone;
    }

    /**
     * 这才是真正的转字符串的函数
     * */
    abstract public String toString(T params);
}
