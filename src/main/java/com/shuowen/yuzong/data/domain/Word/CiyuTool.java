package com.shuowen.yuzong.data.domain.Word;

public class CiyuTool
{
    private static int lcss(String a, String b)
    {
        int[][] dp = new int[a.length() + 1][b.length() + 1];

        int max = 0;

        for (int i = 1; i <= a.length(); i++)
        {
            for (int j = 1; j <= b.length(); j++)
            {
                if (a.charAt(i - 1) == b.charAt(j - 1))
                {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                    max = Math.max(max, dp[i][j]);
                }
            }
        }

        return max;
    }

    public static double weight(String query, String target)
    {
        int l = lcss(query, target);
        int q = query.length();
        int t = target.length();

        double qc = 1.0 * l / q;
        double tc = 1.0 * l / t;

        // 优先比较大的那个，再比较较小的那个
        return Math.max(qc, tc) + Math.min(qc, tc) * 1e-4;
    }

    public static boolean match(String query, String target)
    {
        int l = lcss(query, target);
        int q = query.length();
        int t = target.length();

        double qc = 1.0 * l / q;
        double tc = 1.0 * l / t;

        // 一边必须大于50%
        return qc > 0.5 || tc > 0.5;
    }
}
