package com.shuowen.yuzong.data.model.Reference;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuowen.yuzong.Tool.FractionIndex;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Twin;
import com.shuowen.yuzong.Tool.format.JsonTool;
import com.shuowen.yuzong.data.domain.Reference.Keyword;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class RefEntity
{
    private String dictionary;
    private String sort;
    private String content;
    private String pageInfo;

    private Integer id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RefEntity()
    {
    }

    public RefEntity(String dictionary, String sort, String content)
    {
        this.dictionary = dictionary;
        this.sort = sort;
        this.content = content;
        this.pageInfo = """
                {"page": 0, "title": "其他"}
                """;
    }

    public RefEntity(String dictionary, FractionIndex sort, String content, Pair<String, Integer> pageInfo)
    {
        this.dictionary = dictionary;
        this.content = content;
        setTheSort(sort);
        setThePageInfo(pageInfo);
    }

    public FractionIndex getTheSort()
    {
        return FractionIndex.of(sort);
    }

    public void setTheSort(FractionIndex sort)
    {
        this.sort = sort.toString();
    }

    public Pair<String, Integer> getThePageInfo()
    {
        Map<String, Object> map = JsonTool.readJson(getPageInfo(), new TypeReference<>() {}, new ObjectMapper());
        return Pair.of((String) map.get("title"), (Integer) map.get("page"));
    }

    public void setThePageInfo(Pair<String, Integer> info)
    {
        this.pageInfo = JsonTool.toJson(Map.of(
                "title", info.getLeft(),
                "page", info.getRight()
        ), new ObjectMapper());
    }

    /**
     * 生成一本书开头结尾的两个标记和一个空白页的开头结尾两个标记
     */
    public static List<RefEntity> initBook(String dictionary)
    {
        var end = FractionIndex.getEndPoint();
        var pnt = FractionIndex.between(end.getLeft(), end.getRight(), 2);
        return List.of(
                new RefEntity(dictionary, end.getLeft().toString(), Keyword.FRONT_OF_BOOK),
                new RefEntity(dictionary, pnt.get(0).toString(), Keyword.FRONT_OF_PAGE),
                new RefEntity(dictionary, pnt.get(1).toString(), Keyword.END_OF_PAGE),
                new RefEntity(dictionary, end.getRight().toString(), Keyword.END_OF_BOOK)
        );
    }

    /**
     * 生成一页开头结尾的两个标记
     */
    public static List<RefEntity> initPage(String dictionary, Twin<FractionIndex> sorts)
    {
        var p = FractionIndex.between(sorts.getLeft(), sorts.getRight(), 2);
        return List.of(
                new RefEntity(dictionary, p.get(0).toString(), Keyword.FRONT_OF_PAGE),
                new RefEntity(dictionary, p.get(1).toString(), Keyword.END_OF_PAGE)
        );
    }

}