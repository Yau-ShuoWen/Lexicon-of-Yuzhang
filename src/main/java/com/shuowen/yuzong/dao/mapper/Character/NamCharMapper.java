package com.shuowen.yuzong.dao.mapper.Character;

import com.shuowen.yuzong.dao.model.Character.CharEntity;
import com.shuowen.yuzong.dao.model.Character.CharSimilar;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.sql.SQLSyntaxErrorException;
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
     * 寻找：字表里的简体字和繁体字、模糊识别表里的简体字和繁体字
     * */
    @Select ("select m.* from NC.nam_char m left join NC.nam_char_similar n on m.id = n.char_id " +
            "where m.hanzi = #{hanzi} or m.hantz = #{hanzi} or n.hanzi = #{hanzi} or n.hantz = #{hanzi}")
    List<CharEntity> findByHanziVague(String hanzi);

    /**
     * @exception SQLSyntaxErrorException 空列表抛出异常
     * @return 返回的 {@code CharSimilar}只有两个值，一个是 {@code char_id} 对应上了字表了的id，
     * 剩下的内容打包成 {@code {"sc":[],"tc":[]} } 格式的JSON
     * */
    @Select ("<script>" +
            "SELECT char_id, " +
            "CONCAT('{ \"sc\" : [', GROUP_CONCAT('\"', hanzi, '\"'), '] , \"tc\":[', GROUP_CONCAT('\"', hantz, '\"'), '] }') " +
            "as info from NC.nam_char_similar where char_id in" +
            "<foreach collection='list' item='item' open='(' separator=',' close=')'> #{item} </foreach>" +
            "group by char_id</script>")
    List<CharSimilar> findSimilar(List<Integer> list);

    /**
     * 插入数据
     * */
    @Insert ("insert into NC.nam_char " +
            "(hanzi, hantz, std_py, special, mul_py, py_explain, ipa_exp, mean, note, refer) values " +
            "(#{hanzi},#{hantz},#{std_py},#{special}, #{mul_py}, #{py_explain}, #{ipa_exp}, #{mean}, #{note}, #{refer})")
    void insert(CharEntity charEntity);
}
