package com.shuowen.yuzong.util.version;

import com.shuowen.yuzong.util.text.UString;
import com.shuowen.yuzong.util.tuple.Twin;
import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.DeltaType;
import com.github.difflib.patch.Patch;
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

        List<String> sourceLines = splitLines(source);
        List<String> targetLines = splitLines(target);
        int[] sourceOffsets = lineOffsets(sourceLines);
        int[] targetOffsets = lineOffsets(targetLines);

        List<TextDiffDelta> changes = new ArrayList<>();
        Patch<String> linePatch = DiffUtils.diff(sourceLines, targetLines);
        int hunkIndex = 0;
        for (AbstractDelta<String> lineDelta : linePatch.getDeltas())
        {
            int sourceLineStart = lineDelta.getSource().getPosition();
            int sourceLineEnd = sourceLineStart + lineDelta.getSource().size();
            int targetLineStart = lineDelta.getTarget().getPosition();
            int targetLineEnd = targetLineStart + lineDelta.getTarget().size();
            int sourceStart = sourceOffsets[sourceLineStart];
            int sourceEnd = sourceOffsets[sourceLineEnd];
            int targetStart = targetOffsets[targetLineStart];
            int targetEnd = targetOffsets[targetLineEnd];
            ChangeType hunkType = changeType(lineDelta.getType());

            String sourceChunk = slice(source, sourceStart, sourceEnd);
            String targetChunk = slice(target, targetStart, targetEnd);
            if (hunkType == ChangeType.MODIFIED)
            {
                addInlineChanges(changes, sourceChunk, targetChunk, sourceStart, targetStart,
                        hunkIndex, hunkType, sourceLineStart, sourceLineEnd,
                        targetLineStart, targetLineEnd);
            }
            else
            {
                changes.add(new TextDiffDelta(
                        hunkType, sourceStart, sourceEnd, targetStart, targetEnd,
                        sourceChunk, targetChunk, hunkIndex, hunkType,
                        sourceLineStart, sourceLineEnd, targetLineStart, targetLineEnd
                ));
            }
            hunkIndex++;
        }

        return new TextDiffResponse(source, target, changes);
    }

    private void addInlineChanges(List<TextDiffDelta> changes, String sourceChunk, String targetChunk,
                                  int sourceOffset, int targetOffset, int hunkIndex, ChangeType hunkType,
                                  int sourceLineStart, int sourceLineEnd,
                                  int targetLineStart, int targetLineEnd)
    {
        UString sourceString = new UString(sourceChunk);
        UString targetString = new UString(targetChunk);
        List<String> sourceChars = sourceString.toCharsList();
        List<String> targetChars = targetString.toCharsList();

        for (ChangeResult<Twin<Integer>> change : UStringCompareUtil.compare(sourceString, targetString))
        {
            Twin<Integer> sourceRange = change.getOldItem();
            Twin<Integer> targetRange = change.getNewItem();
            changes.add(new TextDiffDelta(
                    change.getChangeType(),
                    sourceOffset + sourceRange.getLeft(),
                    sourceOffset + sourceRange.getRight(),
                    targetOffset + targetRange.getLeft(),
                    targetOffset + targetRange.getRight(),
                    String.join("", sourceChars.subList(sourceRange.getLeft(), sourceRange.getRight())),
                    String.join("", targetChars.subList(targetRange.getLeft(), targetRange.getRight())),
                    hunkIndex, hunkType, sourceLineStart, sourceLineEnd,
                    targetLineStart, targetLineEnd
            ));
        }
    }

    private ChangeType changeType(DeltaType type)
    {
        return switch (type)
        {
            case INSERT -> ChangeType.ADDED;
            case DELETE -> ChangeType.DELETED;
            case CHANGE -> ChangeType.MODIFIED;
            default -> throw new IllegalArgumentException("Unsupported delta type: " + type);
        };
    }

    private List<String> splitLines(String text)
    {
        List<String> lines = new ArrayList<>();
        int start = 0;
        for (int index = 0; index < text.length(); index++)
        {
            if (text.charAt(index) == '\n')
            {
                lines.add(text.substring(start, index + 1));
                start = index + 1;
            }
        }
        if (start < text.length() || (!text.isEmpty() && text.endsWith("\n")))
        {
            lines.add(text.substring(start));
        }
        return lines;
    }

    private int[] lineOffsets(List<String> lines)
    {
        int[] offsets = new int[lines.size() + 1];
        for (int index = 0; index < lines.size(); index++)
        {
            offsets[index + 1] = offsets[index] + lines.get(index).codePointCount(0, lines.get(index).length());
        }
        return offsets;
    }

    private String slice(String text, int codePointStart, int codePointEnd)
    {
        int start = text.offsetByCodePoints(0, codePointStart);
        int end = text.offsetByCodePoints(0, codePointEnd);
        return text.substring(start, end);
    }
}
