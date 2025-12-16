package com.shuowen.yuzong.Tool.JavaUtilExtend;

public class ObjectTool
{
    /**
     * 返回自己
     *
     * @apiNote 担心if(true)和if(false)在测试里的问题，就用{@code if(ObjectTool.unchecked(true))} 就可以了
     */
    public static boolean unchecked(boolean b)
    {
        // 小聪明哈，有些地方测试如果是明确的true或者false，编译器会直接报错或报警告，这里可以把他绕晕
        int a = 0;
        if (b) a++;
        return a == 1;
    }

    /**
     * 使用空格输出多个内容
     */
    public static void print(Object... obj)
    {
        for (Object o : obj) System.out.print(o + " ");
        System.out.println();
    }

    /**
     * 使用换行输出多个内容
     */
    public static void println(Object... obj)
    {
        for (Object o : obj) System.out.println(o);
        System.out.println();
    }
}
