package com.shuowen.yuzong.ysw.data.domain.diary;

import com.shuowen.yuzong.ysw.data.model.diary.DiaryCatalogEntity;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class DiaryCatalog
{
    private List<YearGroup> years;

    @Data
    public static class YearGroup
    {
        private Integer year;
        private Integer total;
        private List<MonthGroup> months;

        private YearGroup(Integer year)
        {
            this.year = year;
            this.total = 0;
            this.months = new ArrayList<>();
        }

        private void addMonth(DiaryCatalogEntity item)
        {
            months.add(new MonthGroup(item));
            total += item.getTotal();
        }
    }

    @Data
    public static class MonthGroup
    {
        private Integer month;
        private Integer total;
        private LocalDate startDate;
        private LocalDate endDate;

        private MonthGroup(DiaryCatalogEntity item)
        {
            this.month = item.getMonth();
            this.total = item.getTotal();
            this.startDate = item.getStartDate();
            this.endDate = item.getEndDate();
        }
    }

    public DiaryCatalog(List<DiaryCatalogEntity> list)
    {
        this.years = new ArrayList<>();
        YearGroup current = null;

        for (DiaryCatalogEntity item : list)
        {
            if (current == null || !Objects.equals(current.getYear(), item.getYear()))
            {
                current = new YearGroup(item.getYear());
                years.add(current);
            }
            current.addMonth(item);
        }
    }
}
