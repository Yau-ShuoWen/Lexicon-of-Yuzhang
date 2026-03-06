package com.shuowen.yuzong.Tool.DataVersionCtrl;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;
import com.shuowen.yuzong.Tool.dataStructure.UString;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Twin;

import java.util.ArrayList;
import java.util.List;

public class UStringCompareUtil
{
    public static List<ChangeResult<Twin<Integer>>> compare(UString oldStr, UString newStr)
    {
        Patch<String> patch = DiffUtils.diff(
                UString.toUCharList(oldStr.toString()),
                UString.toUCharList(newStr.toString())
        );
        List<ChangeResult<Twin<Integer>>> list = new ArrayList<>();

        for (AbstractDelta<String> d : patch.getDeltas())
        {
            int os = d.getSource().getPosition();
            int oe = os + d.getSource().size();

            int ns = d.getTarget().getPosition();
            int ne = ns + d.getTarget().size();

            Twin<Integer> oldR = Twin.of(os, oe);
            Twin<Integer> newR = Twin.of(ns, ne);

            ChangeType type = switch (d.getType())
            {
                case INSERT -> ChangeType.ADDED;
                case DELETE -> ChangeType.DELETED;
                case CHANGE -> ChangeType.MODIFIED;
                default -> throw new RuntimeException();
            };

            list.add(new ChangeResult<>(type, oldR, newR));
        }
        return list;
    }
}
