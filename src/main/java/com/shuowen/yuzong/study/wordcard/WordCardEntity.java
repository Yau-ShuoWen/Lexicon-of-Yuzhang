package com.shuowen.yuzong.study.wordcard;

import lombok.Data;

@Data
public class WordCardEntity
{
    public Integer id;
    public String dialect;
    public String putonghua; // ScTcText
    public String word;      // ScTcText
    public String pinyin;    // List<UniPinyin>
}
//create table study_word_card
//(
//    id        int auto_increment
//        primary key,
//    dialect   varchar(3)   not null,
//    word      json         not null,
//    putonghua json         not null,
//    pinyin    varchar(255) not null,
//    word_str  varchar(500) as (json_unquote(`word`)) stored,
//    constraint idx_unique_word
//        unique (dialect, word_str)
//);