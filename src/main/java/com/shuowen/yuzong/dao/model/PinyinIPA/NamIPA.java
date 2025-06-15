package com.shuowen.yuzong.dao.model.PinyinIPA;

import lombok.Data;

@Data
public class NamIPA
{
    private String standard;
    private String ncDict;
    private String GanSum;
    private String ChiDial;
    private String ncRecord;
    private String ncStudy;
    private String ncPhon;
    private String Code;


    /**
     * 将断开的拼音部件合并成完整的拼音
     *
     * 合并过程中如果发生异常，如含有无效的
     */
    public static NamIPA merge(NamIPA a, NamIPA b)
    {
        NamIPA c = new NamIPA();
        try
        {
            c.setNcDict(add(a.getNcDict(), b.getNcDict()));
            c.setGanSum(add(a.getGanSum() , b.getGanSum()));
            c.setChiDial(add(a.getChiDial() , b.getChiDial()));
            c.setNcRecord(add(a.getNcRecord() , b.getNcRecord()));
            c.setStandard(add(a.getStandard() , b.getStandard()));
            c.setNcStudy(add(a.getNcStudy() , b.getNcStudy()));
            c.setNcPhon(add(a.getNcPhon() , b.getNcPhon()));
            c.setCode(add(a.getCode().substring(0, 2) , b.getCode().substring(2, 5)));
        } catch (Exception e)//异常处理：数据库可能没有相关值，但是不会在service层检查，这次合并无效
        {
            c.setNcDict("-");
            c.setGanSum("-");
            c.setChiDial("-");
            c.setNcRecord("-");
            c.setStandard("-");
            c.setNcStudy("-");
            c.setNcPhon("-");
            c.setCode("-");

        }
        return c;
    }

    private static String add(String s1, String s2) {

        String merged = s1 + s2;
        return merged.contains("-") ? "-" : merged;
    }
}
