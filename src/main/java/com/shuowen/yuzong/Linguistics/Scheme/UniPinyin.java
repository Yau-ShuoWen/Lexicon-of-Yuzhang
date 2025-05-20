package com.shuowen.yuzong.Linguistics.Scheme;

import com.shuowen.yuzong.Linguistics.Format.StyleParams;
import lombok.Data;

@Data
abstract public class UniPinyin
{
    // 不包括声调的标准拼音
    protected String pinyin;
    // 数字音调，0表示轻声，null表示这个是没有声调的
    protected Integer tone = null;
    // 号码：需要再计算
    protected String code = null;
    // 最终格式：承载体
    protected String show = "";

    protected static final String INVALID_PINYIN = "[无效]";

    public boolean isInvalid()
    {
        return pinyin == null || show.equals(INVALID_PINYIN);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) // 如果是同一個對象，返回 true
        {
            return true;
        }
        // 如果對象為 null 或類型不同，返回 false
        if (obj == null || getClass() != obj.getClass())
        {
            return false;
        }
        // 比較拼音和音调，其他不用比较
        return (pinyin.equals(((UniPinyin) obj).pinyin))
                && (tone.equals(((UniPinyin) obj).tone));
    }

    @Override
    public int hashCode()
    {
        return pinyin.hashCode() + tone.hashCode();
    }

    /**
     * 信任来源的拼音
     *
     * @param num 是否有数字
     */
    public UniPinyin(String s, boolean num)
    {
        // 这是因为可能查到空的拼音，所以信任的拼音也要检查
        if (s == null)
        {
            pinyin = null;
            tone = null;
            return;
        }
        if (num)
        {
            pinyin = s.substring(0, s.length() - 1);
            tone = s.charAt(s.length() - 1) - '0';
        }
        else
        {
            pinyin = s;
            tone = 0;
        }
    }

    /**
     * 不信任来源的拼音
     */
    public UniPinyin(String s)
    {
        // 空字符串
        if (s == null)
        {
            pinyin = null;
            tone = null;
            return;
        }
        s = s.trim().toLowerCase(); // 可选处理，统一大小写
        // 加上音调 ni3
        if (s.matches("\\p{L}+\\d"))
        {
            pinyin = s.substring(0, s.length() - 1);
            tone = s.charAt(s.length() - 1) - '0';
            //拼音为空 或者 音调不合理
            if (pinyin.isEmpty() || !isToneValid(tone))
            {
                pinyin = null;
                tone = null;
                return;
            }
        }
        // 没有音调 默认音调 de -> de0
        else if (s.matches("\\p{L}+"))
        {
            pinyin = s;
            tone = 0;
        }
        // 以上两种格式都不满足
        else
        {
            pinyin = null;//无效
        }
        if (pinyin == null) return;//掐断，不要为null对象被操作的机会

        scan();//拼音串去除代声母
    }


    abstract protected boolean isToneValid(int n);

    /**
     * 尽可能过滤用户输入
     */
    abstract void scan();

    @Override
    public String toString()
    {
        return " / " + pinyin + tone + " / ";
    }

    abstract public String toString(StyleParams params);

    public String getCode()
    {
        if (code == null)
        {
            if(isInvalid()) return INVALID_PINYIN;
            // 转码，如果失败就返回无效
            toCode();
        }
        return code;
    }

    /**
     * 生成号码，方便数据库排序
     */
    abstract void toCode();
}
