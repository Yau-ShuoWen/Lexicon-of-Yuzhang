package com.shuowen.yuzong.Tool.JavaUtilExtend;

public class NumberTool
{
    public enum RangeType
    {
        OPEN,      // 双开区间
        CLOSED,    // 双闭区间
        LEFT_OPEN, // 左开右闭
        RIGHT_OPEN // 左闭右开
    }

    public static boolean inRange(int value, int min, int max, RangeType type)
    {
        if (min > max) throw new IllegalArgumentException("min不能大于max");

        return switch (type)
        {
            case OPEN -> value > min && value < max;
            case CLOSED -> value >= min && value <= max;
            case LEFT_OPEN -> value > min && value <= max;
            case RIGHT_OPEN -> value >= min && value < max;
        };
    }

    /**
     * 双开区间
     */
    public static boolean openBetween(int value, int min, int max)
    {
        return inRange(value, min, max, RangeType.OPEN);
    }

    /**
     * 双闭区间
     */
    public static boolean closeBetween(int value, int min, int max)
    {
        return inRange(value, min, max, RangeType.CLOSED);
    }

    /**
     * 左闭右开，数组的判断情况
     */
    public static boolean arrayBetween(int value, int min, int max)
    {
        return inRange(value, min, max, RangeType.RIGHT_OPEN);
    }
}
