package com.shuowen.yuzong.util.font;

import com.shuowen.yuzong.util.text.ScTcText;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;

public class ExtractHighUnicode
{
    public static void main(String[] args)
    {
        // 支持命令行参数：java ExtractHighUnicode <sql文件路径> <输出文件路径>
        String sqlFilePath = args.length > 0 ? args[0] : getDefaultSqlPath();
        String outputPath = args.length > 1 ? args[1] : "E:\\Pycharm\\PycharmProjects\\FontTool\\rare_chars.txt";

        System.out.println("========================================");
        System.out.println("生僻字提取工具");
        System.out.println("========================================");
        System.out.println("输入文件: " + sqlFilePath);
        System.out.println("输出文件: " + outputPath);
        System.out.println("----------------------------------------");

        try
        {
            // 检查输入文件是否存在
            if (!Files.exists(Paths.get(sqlFilePath))) {
                System.err.println("❌ 错误: 输入文件不存在 - " + sqlFilePath);
                System.exit(1);
            }

            System.out.println("正在读取SQL文件...");
            String content = Files.readString(Paths.get(sqlFilePath));
            System.out.println("文件大小: " + content.length() + " 字符");

            // 翻译出来的简体字也算（依赖项目工具类）
            System.out.println("正在进行简繁转换...");
            content += ScTcText.offline(content);

            HashSet<String> result = new HashSet<>();

            System.out.println("正在提取生僻字...");
            content.codePoints().forEach(cp ->
            {
                if (cp > 0x10000 && !isEmoji(cp))
                {
                    result.add(new String(Character.toChars(cp)));
                }
            });

            // 写入文件（直接连接所有字符，无分隔符，供pyftsubset使用）
            String joined = String.join("", result);
            Files.writeString(
                    Paths.get(outputPath),
                    joined,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );

            System.out.println("----------------------------------------");
            System.out.println("✅ 提取完成！");
            System.out.println("📊 共找到 " + result.size() + " 个生僻字");
            System.out.println("📁 输出文件: " + outputPath);

            // 显示前10个字符作为预览
            if (result.size() > 0) {
                String preview = joined.length() > 30 ? joined.substring(0, 30) + "..." : joined;
                System.out.println("🔍 预览: " + preview);
            }
            System.out.println("========================================");

        } catch (IOException e)
        {
            System.err.println("❌ 错误: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * 获取默认的SQL文件路径（基于当天日期）
     */
    private static String getDefaultSqlPath()
    {
        String today = java.time.LocalDate.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "E:\\NCbackup" + today + ".sql";
    }

    /**
     * 判断一个Unicode码点是否为Emoji字符
     */
    private static boolean isEmoji(int cp)
    {
        return (cp >= 0x1F600 && cp <= 0x1F64F) ||  // 表情符号
                (cp >= 0x1F300 && cp <= 0x1F5FF) ||  // 杂项符号
                (cp >= 0x1F680 && cp <= 0x1F6FF) ||  // 交通符号
                (cp >= 0x1F700 && cp <= 0x1F77F) ||  // 炼金术符号
                (cp >= 0x1F780 && cp <= 0x1F7FF) ||  // 几何形状
                (cp >= 0x1F800 && cp <= 0x1F8FF) ||  // 补充箭头
                (cp >= 0x1F900 && cp <= 0x1F9FF) ||  // 补充符号
                (cp >= 0x1FA00 && cp <= 0x1FA6F) ||  // 象棋符号
                (cp >= 0x1FA70 && cp <= 0x1FAFF) ||  // 符号扩展
                (cp >= 0x2600 && cp <= 0x27BF) ||    // 杂项符号
                (cp >= 0xFE00 && cp <= 0xFE0F) ||    // 变体选择器
                (cp >= 0x1F1E6 && cp <= 0x1F1FF) ||  // 区域指示符
                (cp == 0x200D) ||                     // 零宽连字符
                (cp == 0x20E3) ||                     // 组合键帽
                (cp >= 0xE0020 && cp <= 0xE007F);     // 标签符号
    }
}