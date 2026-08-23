package com.shuowen.yuzong.user.data.domain;

public final class AuthorityCode
{
    private AuthorityCode()
    {
    }

    public static final String BLOG_READ_PUBLIC = Authority.BLOG_READ_PUBLIC.code();
    public static final String BLOG_READ_FRIENDS = Authority.BLOG_READ_FRIENDS.code();
    public static final String BLOG_READ_PRIVATE = Authority.BLOG_READ_PRIVATE.code();
    public static final String DEV_ACCESS = Authority.DEV_ACCESS.code();
    public static final String DICT_EDIT_VIEW = Authority.DICT_EDIT_VIEW.code();
    public static final String DICT_EDIT_WRITE = Authority.DICT_EDIT_WRITE.code();
    public static final String DICT_ACCESS_LAC = Authority.DICT_ACCESS_LAC.code();
    public static final String DICT_ACCESS_CED = Authority.DICT_ACCESS_CED.code();
    public static final String ADMIN_ACCESS = Authority.ADMIN_ACCESS.code();
}
