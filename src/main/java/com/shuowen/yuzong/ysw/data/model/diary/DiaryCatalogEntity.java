package com.shuowen.yuzong.ysw.data.model.diary;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DiaryCatalogEntity
{
    private Integer year;
    private Integer month;
    private Integer total;
    private LocalDate startDate;
    private LocalDate endDate;
}
