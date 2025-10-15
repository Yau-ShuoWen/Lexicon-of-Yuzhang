package com.shuowen.yuzong.dao.model.Word;

import lombok.Data;

@Data
public class WordSimilar
{
    Integer id;
    Integer wordId;
    String ciyu;
    String tszyu;
    String info;
}
