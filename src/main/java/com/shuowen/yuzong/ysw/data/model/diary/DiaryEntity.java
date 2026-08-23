package com.shuowen.yuzong.ysw.data.model.diary;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class DiaryEntity
{
   private LocalDate date;
   private String content;
   private LocalDate startDate;
   private LocalDate finalizeDate;
   private String forFriend;//朋友，为null时说明不存在给朋友版本
   private String forStranger;//公开，为null是说明不存在公开版本

   private Integer id;
   private Integer sort;
   private LocalDateTime createdTime;
   private LocalDateTime updatedTime;
}
