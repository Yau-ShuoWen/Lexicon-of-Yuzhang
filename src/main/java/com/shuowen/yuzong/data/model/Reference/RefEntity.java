package com.shuowen.yuzong.data.model.Reference;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shuowen.yuzong.Tool.DataVersionCtrl.ChangeDetectable;
import com.shuowen.yuzong.Tool.FractionIndex;
import com.shuowen.yuzong.Tool.dataStructure.text.ScTcText;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Twin;
import com.shuowen.yuzong.Tool.format.JsonTool;
import com.shuowen.yuzong.data.domain.Reference.DictCode;
import com.shuowen.yuzong.data.domain.Reference.Keyword;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Data
@NoArgsConstructor
public class RefEntity implements ChangeDetectable<RefEntity>
{
    private String dictionary;  // 辞书代号
    private String sort;        // 顺序
    private String content;     // 原始内容
    private String pageInfo;    // 页面信息

    private String text;    // 编辑类特有：简繁版本
    private String note;    // 编辑类特有：注释

    private Boolean locked;

    private Integer id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 内部初始化。只有辞书、排序、控制内容三个字段
     */
    private RefEntity(DictCode dict, FractionIndex sort, String content)
    {
        this.dictionary = dict.getCode();
        setTheSort(sort);
        this.content = content;

        this.pageInfo = "{\"page\": 0, \"title\": \"其他\"}";
        this.text = ScTcText.emptyJson();
        this.note = ScTcText.emptyJson();
        locked = false;
    }

    /**
     * 草稿初始化。只有辞书，排序，控制和内容字段，页面信息四个字段
     */
    public RefEntity(DictCode dict, FractionIndex sort, String content, Pair<String, Integer> pageInfo)
    {
        this.dictionary = dict.getCode();
        setTheSort(sort);
        this.content = content;
        setThePageInfo(pageInfo);

        this.text = ScTcText.emptyJson();
        this.note = ScTcText.emptyJson();
        locked = false;
    }

    public DictCode getTheDict()
    {
        return new DictCode(dictionary);
    }

    public void setTheDict(DictCode dict)
    {
        this.dictionary = dict.toString();
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
        Map<String, Object> map = JsonTool.readJson(getPageInfo(), new TypeReference<>() {});
        return Pair.of((String) map.get("title"), (Integer) map.get("page"));
    }

    public void setThePageInfo(Pair<String, Integer> info)
    {
        this.pageInfo = JsonTool.toJson(Map.of(
                "title", info.getLeft(),
                "page", info.getRight()
        ));
    }

    /**
     * 生成一本书开头结尾的两个标记和一个空白页的开头结尾两个标记
     */
    public static List<RefEntity> initBook(DictCode dict, int pageCnt)
    {
        var sorts = FractionIndex.rebuild(2 + (pageCnt * 2));
        List<RefEntity> list = new ArrayList<>(2 + (pageCnt * 2));

        list.add(new RefEntity(dict, sorts.get(0), Keyword.FRONT_OF_BOOK));
        for (int i = 1; i < sorts.size() - 1; i += 2)
        {
            list.add(new RefEntity(dict, sorts.get(i), Keyword.FRONT_OF_PAGE));
            list.add(new RefEntity(dict, sorts.get(i + 1), Keyword.END_OF_PAGE));
        }
        list.add(new RefEntity(dict, sorts.get(sorts.size() - 1), Keyword.END_OF_BOOK));

        return list;
    }

    /**
     * 生成一页开头结尾的两个标记
     */
    public static List<RefEntity> initPage(DictCode dict, Twin<FractionIndex> sorts)
    {
        var p = FractionIndex.between(sorts.getLeft(), sorts.getRight(), 2);
        return List.of(
                new RefEntity(dict, p.get(0), Keyword.FRONT_OF_PAGE),
                new RefEntity(dict, p.get(1), Keyword.END_OF_PAGE)
        );
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof RefEntity refEntity)) return false;
        return Objects.equals(id, refEntity.id);
    }

    @Override
    public boolean isNewItem()
    {
        return id == null;
    }

    @Override
    public List<String> getChangedFields(RefEntity other)
    {
        List<String> diff = new ArrayList<>();
        if (!Objects.equals(content, other.content)) diff.add("content");
        if (!Objects.equals(text, other.text)) diff.add("text");
        if (!Objects.equals(note, other.note)) diff.add("note");
        if (!Objects.equals(pageInfo, other.pageInfo)) diff.add("pageInfo");
        return diff;
    }

    @Override
    public Object getUniqueKey()
    {
        return id;
    }

}