package com.shuowen.yuzong.data.dto;

import lombok.Data;

import java.util.*;

/**
 * 搜索结果的预览信息，无论是汉字、词语还是其他都是这个
 */
@Data
public class SearchResult
{
    String title;  // 结果的主体
    String exlain; // 结果的简短说明
    String tag;    // 结果的标签
    Map<String,String> info; //反查的信息

    public SearchResult(String title, String exlain, String tag, Map<String, String> info)
    {
        this.title = title;
        this.exlain = exlain;
        this.tag = tag;
        this.info = info;
    }
}
