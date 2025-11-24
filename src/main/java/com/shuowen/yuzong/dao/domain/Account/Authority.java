package com.shuowen.yuzong.dao.domain.Account;

public enum Authority
{

    /*
     * 编辑词条权限
     * 审核词条权限
     * 争议词汇查看权限
     * 提交反馈权限
     * 校对文档权限
     * 管理员权限
     * */


    EDIT("edit"),
    AUDIT("audit"),
    SENSITIVE("sensitive"),
    FEEDBACK("feecback"),
    PROOFREAD("proofread"),
    ADMIN("admin"),
    STUDY("study"),
    NO("no");

    private String code;

    Authority(String code)
    {
        this.code = code.trim().toLowerCase();
    }

    public static Authority of(String code)
    {
        for (var i:values())
            if(i.code.equals(code)) return i;
        return NO;
    }
}
