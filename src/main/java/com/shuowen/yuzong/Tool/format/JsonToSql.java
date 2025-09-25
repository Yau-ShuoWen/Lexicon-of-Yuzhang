package com.shuowen.yuzong.Tool.format;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.shuowen.yuzong.Tool.dataStructure.Quadruple;


import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class JsonToSql
{
    // 批量插入的大小，可以根据需要调整
    private static final int BATCH_SIZE = 1000;

    public static void main(String[] args)
    {
        //待填入
        String inputFile = "word.json";
        String outputFile = "word.sql";
        String errorLogFile = "error.log";
        String table = "word_dict";

        try
        {
            ObjectMapper objectMapper = new ObjectMapper();

            // 读取JSON文件
            List<Map<String, Object>> list = objectMapper.readValue(
                    new File(inputFile),
                    new TypeReference<>() {}
            );

            FileWriter sqlWriter = new FileWriter(outputFile);
            FileWriter errorWriter = new FileWriter(errorLogFile);

            // 写入文件头
            sqlWriter.write("-- 词典数据插入脚本\n");
            sqlWriter.write("-- 生成时间: " + new java.util.Date() + "\n");
            sqlWriter.write("-- 总记录数: " + list.size() + "\n");
            sqlWriter.write("-- 批量大小: " + BATCH_SIZE + "\n\n");

            errorWriter.write("-- 失败记录日志\n");
            errorWriter.write("-- 生成时间: " + new java.util.Date() + "\n\n");

            int successCount = 0;
            int errorCount = 0;

            // 用于批量构建INSERT语句的StringBuilder
            StringBuilder batchBuilder = new StringBuilder();
            int batchCount = 0;

            /*
             * 配置，具体修改
             * 列名,类型,是否必须非空
             * 示例
             * ImmutableTriple.of("str1", "string", true),
             * ImmutableTriple.of("json1", "json", false)
             * ImmutableTriple.of("json1", "num", false)
             * */
            List<Quadruple<String, String, Boolean, String>> config = List.of(
                    Quadruple.of("word", "str", true, "word"),
                    Quadruple.of("pinyin", "str", true,"pinyin"),
                    Quadruple.of("abbr", "str", false,"abbr"),
                    Quadruple.of("explanation", "str", false,"explanation"),
                    Quadruple.of("source", "json", false,"source"),
                    Quadruple.of("quote", "json", false,"quote"),
                    Quadruple.of("story", "json", false,"story"),
                    Quadruple.of("similar", "json", false,"similar"),
                    Quadruple.of("opposite", "json", false,"opposite"),
                    Quadruple.of("example", "str", false,"example"),
                    Quadruple.of("usage", "str", false,"usagee"),
                    Quadruple.of("notice", "str", false,"notice"),
                    Quadruple.of("spelling", "json", false,"spelling")
            );

            for (int i = 0; i < list.size(); i++)
            {
                Map<String, Object> item = list.get(i);
                List<String> valueList = new ArrayList<>();

                try
                {
                    for (var configItem : config)
                    {
                        String key = configItem.getN1();
                        String type = configItem.getN2();
                        boolean required = configItem.getN3();

                        Object rawValue = item.get(key);

                        if (required)
                        {
                            if (rawValue == null)
                                throw new NoSuchElementException("第" + (i + 1) + "条记录: " + configItem.getN1() + "字段为空或null - " + item + "\n");
                            if (rawValue instanceof String && ((String) rawValue).trim().isEmpty())
                                throw new NoSuchElementException("第" + (i + 1) + "条记录: " + configItem.getN1() + "字段为空或null - " + item + "\n");
                        }

                        String value;
                        if (rawValue == null) value = "NULL";
                        else
                        {
                            if ("json".equals(type))
                                value = "'" + escapeSql(objectMapper.writeValueAsString(rawValue)) + "'";
                            else if ("num".equals(type)) value = String.valueOf(rawValue);
                            else value = "'" + escapeSql(String.valueOf(rawValue)) + "'";
                        }
                        valueList.add(value);
                    }


                    // 如果是批量中的第一条记录，添加INSERT语句前缀
                    if (batchCount == 0)
                    {
                        batchBuilder.append("INSERT INTO ").append(table).append(" (");
                        for (var col : config)
                        {
                            batchBuilder.append(col.getN4()).append(", ");
                        }
                        batchBuilder.setLength(batchBuilder.length() - 2);
                        batchBuilder.append(") VALUES\n");
                    }
                    else
                    {
                        batchBuilder.append(",\n");
                    }

                    batchBuilder.append("  (");
                    for (String v : valueList)
                    {
                        batchBuilder.append(v).append(", ");
                    }
                    batchBuilder.setLength(batchBuilder.length() - 2);
                    batchBuilder.append(") ");
                    batchCount++;
                    successCount++;

                    // 当达到批量大小时，写入文件并重置
                    if (batchCount >= BATCH_SIZE)
                    {
                        batchBuilder.append(";\n\n");
                        sqlWriter.write(batchBuilder.toString());
                        batchBuilder.setLength(0);
                        batchCount = 0;
                    }

                } catch (Exception e)
                {
                    errorCount++;
                    errorWriter.write("第" + (i + 1) + "条记录处理异常: " + item + "\n");
                    errorWriter.write("异常信息: " + e.getMessage() + "\n\n");
                }
            }

            // 处理最后一批未达到BATCH_SIZE的记录
            if (batchCount > 0)
            {
                batchBuilder.append(";\n");
                sqlWriter.write(batchBuilder.toString());
            }

            sqlWriter.write("\n-- 处理完成: 成功 " + successCount + " 条, 失败 " + errorCount + " 条\n");
            sqlWriter.close();

            errorWriter.write("\n-- 总计失败记录: " + errorCount + " 条\n");
            errorWriter.close();

            System.out.println("处理完成！");
            System.out.println("成功记录: " + successCount);
            System.out.println("失败记录: " + errorCount);
            System.out.println("批量大小: " + BATCH_SIZE);
            System.out.println("SQL文件: " + outputFile);
            System.out.println("错误日志: " + errorLogFile);

        } catch (Exception e)
        {
            System.err.println("处理文件时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String escapeSql(String str)
    {
        if (str == null) return "";
        return str.replace("'", "''")
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}
