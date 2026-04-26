package com.shuowen.yuzong.data.domain.Reference;

import com.shuowen.yuzong.Tool.RichTextUtil;
import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import com.shuowen.yuzong.Tool.dataStructure.UString;
import com.shuowen.yuzong.Tool.dataStructure.text.ScTcText;
import com.shuowen.yuzong.data.domain.IPA.IPAData;
import com.shuowen.yuzong.data.model.Reference.RefEntity;
import lombok.Data;

/**
 * 参考资料条目
 */
@Data
public class RefItem
{
    private final UString content;
    private final UString source;

    public RefItem(RefEntity ck, final IPAData data, DictCode dict)
    {
        var l = data.getLanguage();
        var d = data.getDialect();

        {
            var tmp = RichTextUtil.handleRefTitle(new ScTcText(ck.getContent(), d).get(l));
            content = RichTextUtil.format(tmp, data, false, Maybe.exist(dict), true);
        }

        source = new ScTcText(String.format("%s%s第%s頁",
                data.getDictionaryName(DictCode.of(ck.getDictionary())),
                ck.getThePageInfo().getLeft(),
                ck.getThePageInfo().getRight())
        ).get(l);
    }
}
