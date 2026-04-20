package com.shuowen.yuzong.data.domain.Course;

import com.shuowen.yuzong.Tool.JavaUtilExtend.ListTool;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ObjectTool;
import com.shuowen.yuzong.Tool.RichTextUtil;
import com.shuowen.yuzong.Tool.dataStructure.UString;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;

import com.shuowen.yuzong.Tool.format.ObfInt;
import com.shuowen.yuzong.data.domain.IPA.IPAData;
import com.shuowen.yuzong.data.domain.IPA.PinyinOption;
import com.shuowen.yuzong.data.model.Course.CourseEntity;
import lombok.Data;

import java.util.*;

@Data
public class CourseCatalog
{
    private List<Chapter> catalog;

    @Data
    private static class Chapter
    {
        private int chapterId;
        private UString title;
        private UString text;

        private List<Section> sections;

        private Chapter(int chapterId, String title, String text, List<Section> sections, final IPAData data)
        {
            this.chapterId = chapterId;
            this.title = RichTextUtil.easyFormatFromTc(title, data);
            this.text = RichTextUtil.easyFormatFromTc(text, data);
            this.sections = sections;
        }

        public static List<Chapter> listOf(List<CourseEntity> list, final IPAData data)
        {
            if (list.isEmpty()) return new ArrayList<>();
            List<Chapter> ans = new ArrayList<>();

            int idx = 0;
            List<CourseEntity> tmp = new ArrayList<>();
            for (int i = 0; i < list.size(); i++)
            {
                if (Objects.equals(list.get(i).getChapter(), list.get(idx).getChapter()))
                {
                    tmp.add(list.get(i));
                }
                else
                {
                    var edge = list.get(idx);
                    tmp.remove(0);
                    ans.add(new Chapter(edge.getChapter(), edge.getTitle(), edge.getText(),
                            Section.listOf(tmp, data), data));
                    tmp.clear();
                    idx = i;
                    tmp.add(list.get(i));
                }
            }
            var edge = list.get(idx);
            ans.add(new Chapter(edge.getChapter(), edge.getTitle(), edge.getText(), Section.listOf(tmp, data), data));
            return ans;
        }
    }

    @Data
    private static class Section
    {
        private ObfInt id;
        private int sectionId;
        private UString title;

        private Section(int id, int sectionId, String title, final IPAData data)
        {
            this.id = ObfInt.encode(id);
            this.sectionId = sectionId;
            this.title = RichTextUtil.easyFormatFromTc(title, data);
        }

        public static List<Section> listOf(List<CourseEntity> list, final IPAData data)
        {
            if (!ObjectTool.allEqual(list, CourseEntity::getChapter))
                throw new IllegalArgumentException("");

            return ListTool.mapping(list, i -> new Section(i.getId(), i.getSection(), i.getTitle(), data));
        }
    }

    public CourseCatalog(List<CourseEntity> list, Dialect d, Language l)
    {
        var ipaData = new IPAData(l, d, PinyinOption.defaultOf());
        Collections.sort(list);
        catalog = Chapter.listOf(list, ipaData);
    }
}
