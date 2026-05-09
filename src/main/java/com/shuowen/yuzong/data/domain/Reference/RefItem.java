package com.shuowen.yuzong.data.domain.Reference;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shuowen.yuzong.Tool.RichTextUtil;
import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import com.shuowen.yuzong.Tool.dataStructure.UString;
import com.shuowen.yuzong.Tool.dataStructure.text.ScTcText;
import com.shuowen.yuzong.data.domain.IPA.IPAData;
import com.shuowen.yuzong.data.model.Reference.RefEntity;
import lombok.Data;

import static com.shuowen.yuzong.Tool.format.JsonTool.readJson;

/**
 * 参考资料条目
 */
@Data
public class RefItem
{
    private final UString content;
    private final UString source;
    private final UString note;

    public RefItem(RefEntity ck, final IPAData data)
    {
        var l = data.getLanguage();
        var d = data.getDialect();
        var dict= DictCode.of(ck.getDictionary());

        {
            var tmp = RichTextUtil.handleRefTitle(new ScTcText(ck.getContent(), d).get(l));
            content = RichTextUtil.format(tmp, data, false, Maybe.exist(dict), true);
        }

        source = new ScTcText(String.format("%s%s第%s頁",
                data.getDictName(dict),
                ck.getThePageInfo().getLeft(),
                ck.getThePageInfo().getRight())
        ).get(l);

        {
            var tmp = readJson(ck.getNote(), new TypeReference<ScTcText>() {}).get(l);
            tmp = RichTextUtil.handleRefTitle(tmp);
            note = RichTextUtil.format(tmp, data, false, Maybe.exist(dict), true);
        }
    }
}
