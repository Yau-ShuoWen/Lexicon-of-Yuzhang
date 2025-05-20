package com.shuowen.yuzong.dao.mapper.Character;

import com.shuowen.yuzong.dao.model.Character.NamChar;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface NamCharMapper
{
    @Select ("select * from NC.nam_char where id = #{id}")
    NamChar selectByPrimaryKey(Integer id);

    @Select ("select * from NC.nam_char where hanzi = #{hanzi} ")
    List<NamChar> findByHanziExactly(String hanzi);

    @Select ("select hanzi,std_py,fitting_hanzi,special from NC.nam_char " +
            "where hanzi = #{hanzi} " +
            "or fit0 = #{hanzi} " +
            "or fit1 = #{hanzi} " +
            "or fit2 = #{hanzi} " +
            "or fit3 = #{hanzi} " +
            "or fit4 = #{hanzi} " +
            "order by special")
    List<NamChar> findByHanziVague(String hanzi);

//    @Select ("select * from NC.nam_char where std_py like concat(#{pinyin}, '%')")
//    List<NamChar> selectByPinyin(String pinyin);
}
