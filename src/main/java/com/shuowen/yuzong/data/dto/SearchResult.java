package com.shuowen.yuzong.data.dto;

import lombok.Data;

import java.util.*;

/**
 * 搜索结果的预览信息，无论是汉字、词语还是其他都是这个
 */
@Data
public class SearchResult
{
    private String title;     // 结果的主体
    private String explain;   // 结果的简短说明
    private String tag;       // 结果的标签
    private Map<String,String> info; //反查的信息
}
