package com.shuowen.yuzong.util.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 构造前端富文本解析器支持的表格文本。
 */
public class RichTextTable
{
    public enum Alignment
    {
        LEFT, CENTER, RIGHT
    }

    public enum Width
    {
        NORMAL, NOWRAP, LONG
    }

    private record Column(String header, Alignment alignment, Width width)
    {
    }

    private final List<Column> columns = new ArrayList<>();
    private final List<List<String>> rows = new ArrayList<>();
    private boolean leftHeader;

    /** 无顶部表头时，将第一列标记为左侧表头。 */
    public RichTextTable leftHeader()
    {
        if (!rows.isEmpty()) throw new IllegalStateException("添加数据行后不能设置左侧表头");
        leftHeader = true;
        return this;
    }

    /** 添加没有表头文字的列。 */
    public RichTextTable addColumn(Alignment alignment, Width width)
    {
        return addColumnInternal(null, alignment, width);
    }

    /** 添加带表头文字的列。 */
    public RichTextTable addColumn(String header, Alignment alignment, Width width)
    {
        return addColumnInternal(header == null ? null : formatCell(header), alignment, width);
    }

    private RichTextTable addColumnInternal(String header, Alignment alignment, Width width)
    {
        if (!rows.isEmpty()) throw new IllegalStateException("添加数据行后不能再添加列");
        if (alignment == null) throw new IllegalArgumentException("列对齐方式不能为空");
        if (width == null) throw new IllegalArgumentException("列宽类型不能为空");
        columns.add(new Column(header, alignment, width));
        return this;
    }

    /** 添加一行；不足的单元格会在输出时自动补空。 */
    public RichTextTable addRow(Object... cells)
    {
        if (columns.isEmpty()) throw new IllegalStateException("请先添加列");
        if (cells == null) cells = new Object[0];
        if (cells.length > columns.size())
            throw new IllegalArgumentException("单元格数量不能超过列数");

        rows.add(Arrays.stream(cells)
                .map(cell -> formatCell(cell == null ? "" : String.valueOf(cell)))
                .toList());
        return this;
    }

    @Override
    public String toString()
    {
        if (columns.isEmpty()) return "";

        List<String> lines = new ArrayList<>();
        if (columns.stream().anyMatch(column -> column.header() != null))
            lines.add(buildRow(columns.stream()
                    .map(column -> column.header() == null ? "" : column.header())
                    .toList()));

        if (leftHeader && columns.stream().anyMatch(column -> column.header() != null))
            throw new IllegalStateException("左侧表头不能与顶部表头同时使用");
        List<String> alignmentRow = new ArrayList<>(columns.stream().map(RichTextTable::formatColumn).toList());
        if (leftHeader) alignmentRow.set(0, alignmentRow.get(0) + "$");
        lines.add(buildRow(alignmentRow));
        for (List<String> row : rows) lines.add(buildRow(row));
        return String.join("\n", lines);
    }

    private String buildRow(List<String> cells)
    {
        StringBuilder result = new StringBuilder("|");
        for (int i = 0; i < columns.size(); i++)
        {
            String cell = i < cells.size() ? cells.get(i) : "";
            result.append(" ").append(cell).append(" |");
        }
        return result.toString();
    }

    private static String formatColumn(Column column)
    {
        String marker = switch (column.width())
        {
            case NORMAL -> "";
            case NOWRAP -> "!";
            case LONG -> "*";
        };
        return switch (column.alignment())
        {
            case LEFT -> "---" + marker;
            case CENTER -> ":---" + marker + ":";
            case RIGHT -> "---" + marker + ":";
        };
    }

    private static String formatCell(String cell)
    {
        if (cell.indexOf('|') >= 0)
            throw new IllegalArgumentException("富文本表格的单元格不能包含竖线：" + cell);
        return cell.replace("\r\n", "\\\\")
                .replace("\r", "\\\\")
                .replace("\n", "\\\\");
    }
}
