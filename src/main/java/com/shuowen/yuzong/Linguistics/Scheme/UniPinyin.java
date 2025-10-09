package com.shuowen.yuzong.Linguistics.Scheme;

import com.shuowen.yuzong.Linguistics.Format.PinyinStyle;
import lombok.Getter;

import java.util.*;
import java.util.Objects;
import java.util.function.Function;

abstract public class UniPinyin<T extends PinyinStyle>
{
    // 不包括声调的标准拼音
    protected String pinyin = null;
    // 数字音调，0表示轻声，null表示这个是没有声调的
    protected Integer tone = null;
    // 拼音编码
    protected String code = null;
    // 最终格式：承载体
    protected String show = "";

    @Getter
    protected boolean valid = false;


    /**
     * @implNote 初始化的时候{@code pinyin} {@code code} {@code valid} 这三个参数都为 {@code false/null}，所以<p>
     * - 在初始化的时候，只需要在有效的时候手动调整{@code true}就可以了<p>
     * - 在无效的时候不可以调用，否则发生{@code null}错误
     */

    protected static final String INVALID_PINYIN = "[无效]";

    public String getPinyin()
    {
        return isValid() ? pinyin : INVALID_PINYIN;
    }

    public Integer getTone()
    {
        return isValid() ? tone : -1;
    }

    public String getCode()
    {
        return isValid() ? code : INVALID_PINYIN;
    }

    public static String getError()
    {
        return INVALID_PINYIN;
    }

    /**
     * 返回code里从头开始多少位是声母编码
     *
     * @apiNote 剩下的就是介韵母的编码长度了
     * */
    public abstract Integer shengmuLength();

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
     * 构造函数：信任来源的拼音，如数据库
     *
     * @param num 字符串 {@code s}中是否包含数字音调？<p>
     *            - {@code true}包含，如 jiu3 <p>
     *            - {@code false}不包含，如 la：音调设置为0
     */
    public UniPinyin(String s, boolean num)
    {
        // 这是因为可能查到空的拼音，所以信任的拼音也要检查
        if (s == null) return;
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

        //信任来源地拼音，只需要不是null，就认为是有效的
        valid = true;
    }

    /**
     * 不信任来源的拼音，如用户输入
     */
    public UniPinyin(String s)
    {
        // 空字符串
        if (s == null) return;
        s = s.trim().toLowerCase(); // 可选处理，统一大小写

        int lastChar = s.charAt(s.length() - 1);
        if (lastChar >= '0' && lastChar <= '9')
        {
            pinyin = s.substring(0, s.length() - 1);
            tone = lastChar - '0';
        }
        // 没有音调 默认音调 de -> de0
        else
        {
            pinyin = s;
            tone = 0;
        }

        // 拼音串格式化
        scan();

        /* 检查有效性
         * 拼音：
         * 1. 编码的过程是否顺利？ toCode()函数，如果中途发生字符串等错误，无效，直接返回
         * 2. 获得的编码是否可逆？ encodeable()函数，如果不可逆，无效，直接返回
         *
         * 音调：
         * 1. 音调是否符合范围？和韵尾的搭配是否合理？ isToneValid()函数
         * */
        try
        {
            toCode();
        } catch (Exception e)
        {
            return;
        }

        if (!encodable()) return;
        if (!toneValid()) return;

        valid = true;
    }

    /**
     * 生成这个类对应的默认格式
     * */
    abstract protected T defaultStyle();


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
     * 如果子类没有重写，就简单的组合一下，这个{@code //拼音//}是为了在前端正确渲染做的
     */
    @Override
    public String toString()
    {
        return " // " + pinyin + tone + " // ";
    }

    /**
     * 带上复杂的个性化参数，根据子类而定
     */
    abstract public String toString(T params);


    /**
     * 尽可能过滤用户输入，不同拼音不同风格，交给子类完成
     */
    protected abstract void scan();


    /**
     * 将一个音节字符串转码为一个字符串，便于音素结构的分析与处理。
     */
    protected abstract void toCode();


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


    /**
     * 静态方法：字符串快速格式化为拼音
     * */
    protected static <P extends UniPinyin<S>, S extends PinyinStyle>
    String formatting(String s, Function<String, P> creator, S style)
    {
        // 因为内部有完善的机制处理null等情况，所以这里不需要检查
        // 所以直接创建并且按照style格式化即可
        return creator.apply(s).toString(style);
    }

    /**
     * 静态方法：一列字符串读取为一个拼音的数组，之间使用分隔符
     */
    protected static <P extends UniPinyin<S>, S extends PinyinStyle>
    List<P> toPinyinList(String s, Function<String, P> creator, String separator)
    {
        if (s == null || s.trim().isEmpty()) return new ArrayList<>();

        String[] arr = s.split(separator);
        List<P> list = new ArrayList<>();
        for (String str : arr)
        {
            if (!str.trim().isEmpty())
            {
                P pinyin = creator.apply(str.trim());
                list.add(pinyin);
            }
        }
        return list;
    }

    /**
     * 静态方法：拼音数组格式化为字符串数组
     * */
    protected static <P extends UniPinyin<S>, S extends PinyinStyle>
    List<String> toList(List<P> list, S style)
    {
        List<String> ans = new ArrayList<>();
        for (P i : list) ans.add(i.toString(style));
        return ans;
    }

    /**
     * 静态方法渲染：渲染使用一种分隔符（如空格）包围的字符串，并且按照原来的顺序转化
     * @implNote {@code fung1 qieu2 ia5 pok6}转换为{@code  //fung1  qieu2  ia5  pok6// }
     * */
    protected static <P extends UniPinyin<S>, S extends PinyinStyle>
    String splitAndReplace(String s, Function<String, P> creator, S style, String separator)
    {
        var list = toPinyinList(s, creator, separator);

        StringBuilder sb = new StringBuilder();
        for (P i : list) sb.append(i.toString(style) + separator);
        return sb.toString().replace("//   //", "  ");
    }

    /**
     * 静态方法：渲染使用一对分隔符（如[]）包围的字符串，并且按照原来的顺序转化
     * @implNote {@code [fung1][qieu2][ia5][pok6]}转换为{@code  //fung1  qieu2  ia5  pok6// }
     */
    protected static <P extends UniPinyin<S>, S extends PinyinStyle>
    String parseAndReplace(String str, Function<String, P> creator, S style, String start, String end)
    {
        StringBuilder res = new StringBuilder();
        int i = 0;

        while (true)
        {
            int open = str.indexOf(start, i);
            if (open == -1)
            {
                res.append(str.substring(i));
                break;
            }
            int close = str.indexOf(end, open + start.length());
            if (close == -1)
            {
                res.append(str.substring(i));
                break;
            }
            res.append(str, i, open);
            res.append(formatting(str.substring(open + start.length(), close), creator, style));
            i = close + end.length();
        }

        return res.toString().replace("//  //", "  ");
    }
}
