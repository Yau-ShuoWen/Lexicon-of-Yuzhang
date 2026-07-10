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
   private String abridge;

   private Integer id;
   private LocalDateTime createdTime;
   private LocalDateTime updatedTime;
}
