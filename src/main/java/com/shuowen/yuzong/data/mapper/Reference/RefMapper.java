package com.shuowen.yuzong.data.mapper.Reference;

import com.shuowen.yuzong.data.model.Reference.RefEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RefMapper
{
    /**
     * 字典是否初始化
     */
    Boolean isDictionaryExist(String dictionary);

    /**
     * 字典是否是空的
     */
    Boolean isDictionaryNotEmpty(String dictionary);

    /**
     * 根据关键词寻找词条
     */
    List<RefEntity> getItemsByQuery(String dictionary, String query);

    /**
     * 根据词条寻找页面
     */
    List<RefEntity> getPageBySort(String dictionary, String sort);

    /**
     * 插入数据
     */
    void insert(List<RefEntity> list);

    /**
     * 寻找对应界面旁边的界面的一条数据
     *
     * @param before 决定找到的位置
     *               <br>{@code true} - sort是本页开头的序号，目的是找到上一个界面的结尾的序号
     *               <br>{@code false} - sort是本页结尾的序号，目的是找到下一个界面的开头的序号
     * @return 返回id，不对内容筛选，在应用层筛选
     * <br> 如果插入页面，就可以是书的边界
     * <br> 如果是跳转，就不可以是书的边界
     */
    RefEntity findNearby(String dictionary, String sort, boolean before);

    void updateEdge(RefEntity ref);

    void deleteEdge(String dictionary, String sort);

    /**
     *
     */
    void deleteInside(String dictionary, String frontSort, String endSort);

    Integer findRowCountInReferTable(String dialect);
}
