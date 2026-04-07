package com.shuowen.yuzong.data.domain.Reference;

import com.shuowen.yuzong.Tool.RichTextUtil;
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

    public RefItem(RefEntity ck, final IPAData data)
    {
        var l = data.getLanguage();
        var d = data.getDialect();

        {
            var tmp = RichTextUtil.handleRefTitle(new ScTcText(ck.getContent(), d).get(l),
                    data.getPinyinOption().getPhonogram());
            content = RichTextUtil.format(tmp, data, false);
        }

        source = new ScTcText(String.format("%s%s第%s頁",
                data.getDictionaryName(DictCode.of(ck.getDictionary())),
                ck.getThePageInfo().getLeft(),
                ck.getThePageInfo().getRight())
        ).get(l);
    }
}
