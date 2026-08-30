package com.shuowen.yuzong.util.version;

import com.shuowen.yuzong.util.text.UString;
import com.shuowen.yuzong.util.tuple.Twin;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TextDiffService
{
    public TextDiffResponse compare(TextDiffRequest request)
    {
        String source = request == null || request.source() == null ? "" : request.source();
        String target = request == null || request.target() == null ? "" : request.target();

        UString sourceString = new UString(source);
        UString targetString = new UString(target);
        List<String> sourceChars = sourceString.toCharsList();
        List<String> targetChars = targetString.toCharsList();

        List<TextDiffDelta> changes = new ArrayList<>();
        for (ChangeResult<Twin<Integer>> change : UStringCompareUtil.compare(sourceString, targetString))
        {
            Twin<Integer> sourceRange = change.getOldItem();
            Twin<Integer> targetRange = change.getNewItem();

            changes.add(new TextDiffDelta(
                    change.getChangeType(),
                    sourceRange.getLeft(),
                    sourceRange.getRight(),
                    targetRange.getLeft(),
                    targetRange.getRight(),
                    slice(sourceChars, sourceRange),
                    slice(targetChars, targetRange)
            ));
        }

        return new TextDiffResponse(source, target, changes);
    }

    private String slice(List<String> chars, Twin<Integer> range)
    {
        return String.join("", chars.subList(range.getLeft(), range.getRight()));
    }
}
