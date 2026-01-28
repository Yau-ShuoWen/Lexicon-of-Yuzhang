package com.shuowen.yuzong.data.domain.Reference;

import com.shuowen.yuzong.Tool.FractionIndex;
import com.shuowen.yuzong.data.model.Reference.RefEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 参考资料条目
 */
@Data
public class RefItem
{
    private String dictionary;
    private FractionIndex sort;
    private String content;
    private Integer page;

    private Integer id;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    /**
     * 内部构造使用的
     */
    private RefItem(String dictionary, String sort, String content, Integer page)
    {
        this.dictionary = dictionary;
        this.sort = FractionIndex.of(sort);
        this.content = content;
        this.page = page;
    }

    public RefItem(RefEntity ck)
    {
        dictionary = ck.getDictionary();
        sort = FractionIndex.of(ck.getSort());
        content = ck.getContent();
        page = ck.getPage();

        id = ck.getId();
        createAt = ck.getCreatedAt();
        updateAt = ck.getUpdatedAt();
    }

    public RefEntity transfer()
    {
        var ans = new RefEntity();
        ans.setDictionary(dictionary);
        ans.setSort(sort.toString());
        ans.setContent(content);
        ans.setPage(page);

        return ans;
    }
}
