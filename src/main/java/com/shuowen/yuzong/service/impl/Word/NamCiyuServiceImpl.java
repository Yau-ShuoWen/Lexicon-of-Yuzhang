package com.shuowen.yuzong.service.impl.Word;

import com.shuowen.yuzong.data.domain.Word.NamCiyu;
import com.shuowen.yuzong.data.mapper.Word.NamWordMapper;
import com.shuowen.yuzong.service.interfaces.Word.CiyuService;
import com.shuowen.yuzong.service.impl.Pinyin.NamPinyinServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NamCiyuServiceImpl implements CiyuService
{
    /**
     * 查询汉字
     */
    @Autowired
    private NamWordMapper cy;

    /**
     * 查询音标
     */
    @Autowired
    private NamPinyinServiceImpl ipa;

//    /**
//     * 批量获取 IPA 数据
//     */
//    private Map<NamPinyin, Map<String, String>> getIPABatch(Set<NamPinyin> pinyinList)
//    {
//        return ipa.getMultiLine(pinyinList);
//    }

    public List<NamCiyu> getCiyuByScTc(String s)
    {
        return NamCiyu.Listof(cy.findByCiyuScTc(s));
    }

    public List<NamCiyu> getCiyuVague(String s)
    {
        return NamCiyu.Listof(cy.findByCiyuVague(s));
    }

    public List<NamCiyu> getCiyuBySentence(String s)
    {
        return NamCiyu.Listof(cy.findBySentence(s));
    }

}
