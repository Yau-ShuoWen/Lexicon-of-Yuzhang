package com.shuowen.yuzong.dict.pinyin;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface PinyinMapper
{
    @Select("SELECT self_key AS `key`, root_key AS rootKey, title, note FROM NC.${dialect}_ipa_segment WHERE self_key IS NOT NULL ORDER BY code")
    List<Map<String, Object>> getLegacyEntries(String dialect);
    @Select("""
             SELECT self_key as url, title, note, info
                    FROM NC.${dialect}_ipa_segment
                    WHERE BINARY root_key = #{key}
            """)
    List<PinyinItem> getTableItem(String dialect, String key);

    @Select ("SELECT self_key FROM NC.${dialect}_ipa_segment WHERE self_key IS NOT NULL ")
    List<String> getEditKey(String dialect);

    @Select ("SELECT note FROM NC.${dialect}_ipa_segment WHERE self_key = #{key}")
    String getNote(String dialect, String key);

    @Select ("UPDATE NC.${dialect}_ipa_segment SET note = #{note} WHERE self_key = #{key}")
    void updateNote(String dialect, String key, String note);
}
