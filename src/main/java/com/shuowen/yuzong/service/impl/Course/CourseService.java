package com.shuowen.yuzong.service.impl.Course;

import com.shuowen.yuzong.Tool.JavaUtilExtend.ListTool;
import com.shuowen.yuzong.Tool.RichTextUtil;
import com.shuowen.yuzong.Tool.dataStructure.UString;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Twin;
import com.shuowen.yuzong.data.domain.Course.CourseCatalog;
import com.shuowen.yuzong.data.domain.IPA.IPAData;
import com.shuowen.yuzong.data.domain.IPA.PinyinOption;
import com.shuowen.yuzong.data.mapper.Course.CourseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourseService
{
    @Autowired
    CourseMapper m;

    public CourseCatalog getCatalog(Dialect d, Language l)
    {
        return new CourseCatalog(m.getCatalog(d.toString()), d, l);
    }

    public Twin<UString> getArtical(Language l, Dialect d, Integer id)
    {
        var ipaData = new IPAData(l, d, PinyinOption.defaultOf());
        var data = ListTool.checkSizeOne(m.getArticalById(d.toString(), id), "", "");
        return Twin.of(
                RichTextUtil.easyFormatFromTc(data.getTitle(),ipaData),
                RichTextUtil.easyFormatFromTc(data.getText(),ipaData)
        );
    }
}
