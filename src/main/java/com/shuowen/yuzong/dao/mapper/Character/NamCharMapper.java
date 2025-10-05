package com.shuowen.yuzong.dao.mapper.Character;

import com.shuowen.yuzong.dao.model.Character.CharEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface NamCharMapper
{
    /**
     * 通过主键寻找汉字
     * */
    @Select ("select * from NC.nam_char where id = #{id}")
    CharEntity selectByPrimaryKey(Integer id);

    /**
     * 使用简繁体寻找汉字
     * */
    @Select ("select * from NC.nam_char where hanzi = #{hanzi} or hantz = #{hanzi}")
    List<CharEntity> findByHanziScTc(String hanzi);

    /**
     * 使用简繁体、模糊识别寻找汉字
     * */
    @Select ("select * from NC.nam_char where hanzi = #{hanzi} or hantz = #{hanzi} " +
            "union select * from NC.nam_char where similar like CONCAT('%', #{hanzi}, '%')")
    List<CharEntity> findByHanziVague(String hanzi);

    /**
    * 插入数据
    * */
    @Insert ("insert into NC.nam_char " +
            "(hanzi, hantz, std_py, special, similar, mul_py, py_explain, ipa_exp, mean, note, refer) values " +
            "(#{hanzi},#{hantz},#{std_py},#{special}, #{similar}, #{mul_py}, #{py_explain}, #{ipa_exp}, #{mean}, #{note}, #{refer})")
    void insert(CharEntity charEntity);
}
