package com.shuowen.yuzong;

public class Counter
{
    private int success = 0;
    private int fail = 0;

    /**
     * 一般情况下，只有错误的值需要输出对应内容，所以在这里反转结果，一般的用法如下
     * <blockquote><pre>
     *     if(check(检查正确)) 错误处理流程
     * </pre></blockquote>
     */
    public boolean check(boolean b)
    {
        if (b) success++;
        else fail++;
        return !b;
    }

    public void report()
    {
        int sum = success + fail;
        System.out.println("success: " + success + "/" + sum + ", fail: " + fail + "/" + sum);
    }
}
