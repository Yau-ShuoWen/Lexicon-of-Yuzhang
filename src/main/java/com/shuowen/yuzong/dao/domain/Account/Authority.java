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

    /*
    EDIT_ENTRY(),
    AUDIT_ENTRY(),
    SENSITIVE(),
    FEEDBACK(),
    PROOFREAD(),
    ADMIN(),
    */
    ;

    private int code;

    Authority(int code)
    {
        this.code = code;
    }
}
