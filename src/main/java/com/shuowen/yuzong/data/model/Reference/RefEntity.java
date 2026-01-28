package com.shuowen.yuzong.data.model.Reference;

import com.shuowen.yuzong.Tool.FractionIndex;
import com.shuowen.yuzong.data.domain.Reference.Keyword;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RefEntity
{
    private String dictionary;
    private String sort;
    private String content;
    private Integer page;

    private Integer id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RefEntity()
    {
    }

    public RefEntity(String dictionary, String sort, String content, Integer page)
    {
        this.dictionary = dictionary;
        this.sort = sort;
        this.content = content;
        this.page = page;
    }

    /**
     * 生成一本书开头结尾的两个标记和一个空白页的开头结尾两个标记
     */
    public static List<RefEntity> initBook(String dictionary)
    {
        var end = FractionIndex.getEndPoint();
        var pnt = FractionIndex.between(end.getLeft(), end.getRight(), 2);
        return List.of(
                new RefEntity(dictionary, end.getLeft().toString(), Keyword.FRONT_OF_BOOK, -1),
                new RefEntity(dictionary, pnt.get(0).toString(), Keyword.FRONT_OF_PAGE, -1),
                new RefEntity(dictionary, pnt.get(1).toString(), Keyword.END_OF_PAGE, -1),
                new RefEntity(dictionary, end.getRight().toString(), Keyword.END_OF_BOOK, -1)
        );
    }

    /**
     * 生成一页开头结尾的两个标记
     */
    public static List<RefEntity> initPage(String dictionary, FractionIndex prev, FractionIndex next)
    {
        var p = FractionIndex.between(prev, next, 2);
        return List.of(
                new RefEntity(dictionary, p.get(0).toString(), Keyword.FRONT_OF_PAGE, -1),
                new RefEntity(dictionary, p.get(1).toString(), Keyword.END_OF_PAGE, -1)
        );
    }

}