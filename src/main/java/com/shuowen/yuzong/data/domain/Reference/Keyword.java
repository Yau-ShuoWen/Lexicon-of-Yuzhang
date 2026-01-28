package com.shuowen.yuzong.data.domain.Reference;

/**
 * 管理数据库里特殊的标记
 */
public class Keyword
{
    public static final String FRONT_OF_BOOK = "//FRONT//";
    public static final String END_OF_BOOK = "//END//";
    public static final String FRONT_OF_PAGE = "/FRONT/";
    public static final String END_OF_PAGE = "/END/";

    public static boolean isBookEdge(String text)
    {
        return text.equals(FRONT_OF_BOOK) || text.equals(END_OF_BOOK);
    }

    public static boolean isPageEdge(String text)
    {
        return text.equals(FRONT_OF_PAGE) || text.equals(END_OF_PAGE);
    }
}
