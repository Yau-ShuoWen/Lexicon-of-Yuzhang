package com.shuowen.yuzong.dao.model.Character;

import lombok.Data;

@Data
public class CharPinyin
{
    Integer id;
    Integer charId;
    String sc;
    String tc;
    String pinyin;
    Integer sort;
}
