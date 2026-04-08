package com.shuowen.yuzong.data.mapper.Reference;

import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.data.model.Reference.RefEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
     * 随机选取一个词条
     */
    RefEntity getItemByRandom(String dictionary);

    List<Integer> getAllItemId(String dictionary);

    void updateAllSort(String dictionary, List<Pair<Integer, String>> items);

    void recoverSort(String dictionary);

    /**
     * 根据词条寻找页面
     */
    List<RefEntity> getPageBySort(String dictionary, String sort);

    /**
     * 插入数据
     */
    void batchInsert(List<RefEntity> list);

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

    void batchDelete(String dictionary, List<Integer> list);

    void deleteEdge(String dictionary, String sort);

    /**
     *
     */
    void deleteInside(String dictionary, String frontSort, String endSort);

    void update(RefEntity ref);

    void lockPage(@Param ("dictionary") String dictionary, @Param ("list") List<Integer> list);

    /**
     * 找到每一页开头的信息，用于生成跳转连接
     */
    List<RefEntity> findPageinfo(String dictionary);

    List<RefEntity> findByQuery(String dictionary, String query);

    Integer findRowCountInReferTable(String dialect);
}
