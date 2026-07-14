package com.shuowen.yuzong.data.domain.Reference;

import com.shuowen.yuzong.data.model.Reference.RefEntity;

import java.util.List;

public class RefTextTool
{
    private RefTextTool()
    {
    }

    public static void handle(String dict, List<RefEntity> list)
    {
        for (RefEntity i : list)
        {
            switch (dict)
            {
                case "ncdict" ->
                {
                    String s=i.getContent().
                            replace(",", "，").
                            replace("?", "？").
                            replace("!", "！").
                            replace("~", "～").
                            replace(":", "：").
                            replace("話說","話{n 説}").
                            replace("冇","冒").
                            replace("什咩","什哩")
                            ;
                          i.setContent(s);
                }
                case "ncphon" ->
                {

                }
            }
        }
    }
}
