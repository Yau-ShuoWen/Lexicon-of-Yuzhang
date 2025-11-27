package com.shuowen.yuzong.Tool.dataStructure.option;


/**
 * 为了未来的扩展，使得不需要重写所有类似的流程
 * <ul>
 * <li> SC 南昌话 </li>
 * <li> NIL 无效方言</li>
 * </ul>
 */
public enum Dialect
{
    NAM("nam"),   // 南昌话
    NIL("null");  // 无效方言

    private final String code;

    Dialect(String code)
    {
        this.code = code;
    }

    public static Dialect of(String s)
    {
        if (s == null) return NIL;

        s = s.trim().toLowerCase();
        for (var l : values())
            if (l.code.equals(s)) return l;

        return NIL;
    }

    public boolean isValid()
    {
        return !"null".equals(code);
    }
}
