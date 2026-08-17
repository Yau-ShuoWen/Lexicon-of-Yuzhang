package com.shuowen.yuzong.dict.controller.search;

import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.core.Language;
import com.shuowen.yuzong.util.obfuscate.ObfInt;
import com.shuowen.yuzong.dict.data.domain.Course.CourseCatalog;
import com.shuowen.yuzong.dict.service.Course.CourseService;
import com.shuowen.yuzong.util.text.UString;
import com.shuowen.yuzong.util.tuple.Twin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping ("/api/course/")
public class CourseController
{
    @Autowired
    CourseService s;

    @GetMapping ("/catalog/{d}/{l}")
    public CourseCatalog aaa(@PathVariable Dialect d, @PathVariable Language l)
    {
        return s.getCatalog(d,l);
    }

    @GetMapping ("/text/{d}/{l}/{id}")
    public Twin<UString> bbb(@PathVariable Dialect d, @PathVariable Language l,
                            @PathVariable ObfInt id)
    {
        return s.getArtical(l, d, id.decode());
    }
}
