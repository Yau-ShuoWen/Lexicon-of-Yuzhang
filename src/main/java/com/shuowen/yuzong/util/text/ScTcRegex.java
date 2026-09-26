package com.shuowen.yuzong.util.text;

import com.hankcs.hanlp.HanLP;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@Getter
public class ScTcRegex
{
    /** Java 包含匹配正则。 */
    private final String regex;
    /** Java 完整匹配正则。 */
    private final String fullRegex;
    /** MySQL 包含匹配正则。 */
    private final String sqlRegex;
    /** MySQL 完整匹配正则。 */
    private final String fullSqlRegex;

    @Getter(AccessLevel.NONE)
    private final Pattern pattern;
    @Getter(AccessLevel.NONE)
    private final Pattern fullPattern;

    private static final Map<UChar, List<String>> SPECIAL_HANZI_MAP = buildSpecialHanziMap();
    private static final Map<UChar, List<String>> VARIANT_CACHE = new ConcurrentHashMap<>();

    /** 传入待搜索的文本，按 Unicode 代码点逐字展开。 */
    public ScTcRegex(UString str)
    {
        Objects.requireNonNull(str, "str");
        List<List<String>> segments = new ArrayList<>(str.length());
        for (UChar ch : str)
        {
            segments.add(expand(ch));
        }

        regex = toJavaRegex(segments);
        fullRegex = "^" + regex + "$";
        sqlRegex = toMySqlRegex(segments);
        fullSqlRegex = "^" + sqlRegex + "$";
        pattern = Pattern.compile(regex);
        fullPattern = Pattern.compile(fullRegex);
    }

    /** 整个字符串是否完整满足简繁序列。 */
    public boolean matches(String text)
    {
        Objects.requireNonNull(text, "text");
        return fullPattern.matcher(text).matches();
    }

    /** 字符串中是否存在满足简繁序列的部分。 */
    public boolean contains(String text)
    {
        Objects.requireNonNull(text, "text");
        return pattern.matcher(text).find();
    }

    /** 生成不带首尾限制的 Java 正则主体。 */
    private static String toJavaRegex(List<List<String>> segments)
    {
        StringBuilder sb = new StringBuilder();
        for (List<String> seg : segments)
        {
            if (seg.size() == 1)
            {
                sb.append(Pattern.quote(seg.get(0)));
                continue;
            }
            sb.append('(');
            for (int i = 0; i < seg.size(); i++)
            {
                if (i > 0) sb.append("|");
                sb.append(Pattern.quote(seg.get(i))); // Java 安全转义
            }
            sb.append(')');
        }
        return sb.toString();
    }

    /** 生成通过 JDBC/MyBatis 参数传入的 MySQL 8 ICU 正则主体。 */
    private static String toMySqlRegex(List<List<String>> segments)
    {
        StringBuilder sb = new StringBuilder();
        for (List<String> seg : segments)
        {
            if (seg.size() == 1)
            {
                sb.append(escapeForMySqlRegex(seg.get(0)));
                continue;
            }
            sb.append('(');
            for (int i = 0; i < seg.size(); i++)
            {
                if (i > 0) sb.append('|');
                sb.append(escapeForMySqlRegex(seg.get(i)));
            }
            sb.append(')');
        }
        return sb.toString();
    }

    /**
     * 转义单个候选词：
     * 1. 先转义正则元字符（加反斜杠）
     * 2. 再把反斜杠双写，供 MySQL 字符串字面量使用
     * 注意：如果通过 JDBC PreparedStatement 传参，第2步不需要做！
     */
    private static String escapeForMySqlRegex(String word)
    {
        // 正则元字符
        String meta = "\\^$.|?*+()[]{}";
        StringBuilder sb = new StringBuilder();
        for (UChar ch : UString.of(word))
        {
            if (ch.codePoint() <= Character.MAX_VALUE && meta.indexOf(ch.codePoint()) >= 0)
            {
                sb.append('\\'); // 加正则转义
            }
            sb.append(ch);
        }
        // 如果直接拼进 SQL 字符串，需要把 \ 变成 \\（JDBC 参数则不需要）
        return sb.toString();
    }

    private static List<String> expand(UChar source)
    {
        return VARIANT_CACHE.computeIfAbsent(source, ScTcRegex::computeVariants);
    }

    /** 反复合并双向 HanLP 转换和人工表，直到没有新候选。 */
    private static List<String> computeVariants(UChar source)
    {
        LinkedHashSet<String> variants = new LinkedHashSet<>();
        Queue<String> unchecked = new ArrayDeque<>();

        // 简体结果先入集，使“车”和“車”的输出顺序一致。
        addVariant(HanLP.t2s(source.toString()), variants, unchecked);
        addVariant(source.toString(), variants, unchecked);

        while (!unchecked.isEmpty())
        {
            String current = unchecked.remove();
            addVariant(HanLP.s2t(current), variants, unchecked);
            addVariant(HanLP.t2s(current), variants, unchecked);

            if (current.codePointCount(0, current.length()) == 1)
            {
                List<String> special = SPECIAL_HANZI_MAP.get(UChar.of(current));
                if (special != null)
                {
                    for (String value : special)
                    {
                        addVariant(value, variants, unchecked);
                    }
                }
            }
        }
        return List.copyOf(variants);
    }

    private static void addVariant(String value, Set<String> variants, Queue<String> unchecked)
    {
        if (value != null && !value.isEmpty() && variants.add(value))
        {
            unchecked.add(value);
        }
    }

    /** 把每行当作无向关系，并合并不同行之间有交集的等价类。 */
    private static Map<UChar, List<String>> buildSpecialHanziMap()
    {
        Map<UChar, LinkedHashSet<UChar>> graph = new LinkedHashMap<>();
        for (String rawLine : specialHanzi.lines().toList())
        {
            String line = rawLine.trim();
            if (line.isEmpty()) continue;

            String[] sides = line.split("<=>", -1);
            if (sides.length != 2 || sides[0].isEmpty() || sides[1].isEmpty())
            {
                throw new IllegalStateException("简繁补充表格式错误：" + rawLine);
            }

            List<UChar> group = new ArrayList<>();
            for (UChar ch : UString.of(sides[0])) group.add(ch);
            for (UChar ch : UString.of(sides[1])) group.add(ch);
            for (UChar ch : group)
            {
                graph.computeIfAbsent(ch, key -> new LinkedHashSet<>()).addAll(group);
            }
        }

        Map<UChar, List<String>> result = new LinkedHashMap<>();
        Set<UChar> visited = new LinkedHashSet<>();
        for (UChar start : graph.keySet())
        {
            if (!visited.add(start)) continue;

            LinkedHashSet<UChar> component = new LinkedHashSet<>();
            Queue<UChar> unchecked = new ArrayDeque<>();
            unchecked.add(start);
            while (!unchecked.isEmpty())
            {
                UChar current = unchecked.remove();
                component.add(current);
                for (UChar neighbour : graph.get(current))
                {
                    if (visited.add(neighbour)) unchecked.add(neighbour);
                }
            }

            List<String> values = component.stream().map(UChar::toString).toList();
            for (UChar ch : component) result.put(ch, values);
        }
        return Collections.unmodifiableMap(result);
    }

    private static final String specialHanzi = """
            著着<=>著
            兒儿<=>兒
            乾干<=>乾
            夥伙<=>夥
            藉借<=>藉
            瞭了<=>瞭
            馀余<=>餘
            摺折<=>摺
            徵征<=>徵
            画划<=>畫
            鲶鲇<=>鯰
            沈渖<=>瀋
            碱硷<=>鹼
            苹𬞟<=>蘋
            克剋<=>剋
            恶𫫇<=>噁
            钟锺<=>鍾
            合𬮤<=>閤
            
            合<=>合閤
            辟<=>辟闢
            卜<=>卜蔔
            才<=>才纔
            丑<=>丑醜
            出<=>出齣
            当<=>當噹
            党<=>黨党
            淀<=>澱淀
            发<=>發髮
            范<=>范範
            丰<=>豐丰
            谷<=>谷穀
            广<=>廣广
            后<=>後后
            伙<=>夥伙
            获<=>獲穫
            几<=>幾几
            饥<=>飢饑
            姜<=>姜薑
            尽<=>盡儘
            据<=>據据
            卷<=>捲卷
            夸<=>夸誇
            累<=>累纍
            了<=>了瞭
            弥<=>彌瀰
            苹<=>蘋苹
            仆<=>僕仆
            朴<=>朴樸
            签<=>簽籤
            确<=>確确
            舍<=>舍捨
            沈<=>沈瀋
            胜<=>勝胜
            术<=>術术
            涂<=>涂塗
            团<=>團糰
            纤<=>纖縴
            须<=>須鬚
            叶<=>葉叶
            佣<=>傭佣
            余<=>余餘
            吁<=>籲吁
            郁<=>郁鬱
            愿<=>願愿
            云<=>雲云
            脏<=>臟髒
            折<=>折摺
            征<=>征徵
            证<=>證証
            钟<=>鍾鐘
            种<=>種种
            准<=>準准
            柜<=>櫃柜
            家<=>家傢
            价<=>價价
            腊<=>臘腊
            蜡<=>蠟蜡
            帘<=>簾帘
            适<=>適适
            旋<=>旋鏇
            症<=>症癥
            卤<=>鹵滷
            万<=>萬万
            摆<=>擺襬
            恶<=>惡噁
            担<=>擔担
            筑<=>築筑
            坝<=>壩垻
            钥<=>鑰鈅
            钻<=>鑽鉆
            药<=>葯藥
            苧<=>薴苧
            曲<=>曲麯
            苏<=>蘇囌
            汇<=>匯彙
            历<=>歷曆
            斗<=>鬥斗
            回<=>回迴
            面<=>面麵
            板<=>板闆
            表<=>表錶
            别<=>别彆
            冬<=>冬鼕
            刮<=>刮颳
            借<=>借藉
            克<=>克剋
            困<=>困睏
            漓<=>漓灕
            蔑<=>蔑衊
            松<=>松鬆
            咸<=>鹹咸
            御<=>御禦
            制<=>制製
            致<=>致緻
            秋<=>秋鞦
            千<=>千韆
            朱<=>朱硃
            沄<=>沄澐
            芸<=>芸蕓
            胡<=>胡鬍
            里<=>里裏
            向<=>向嚮
            坛<=>壇罎
            划<=>劃划
            鹇<=>鷴鷳
            复<=>復複
            厂<=>厂廠
            剩<=>剩賸
            
            系<=>系係繫
            只<=>只隻衹
            干<=>乾干幹
            
            蒙<=>蒙懞濛矇
            台<=>臺台檯颱
            
            说<=>說説
            为<=>為爲
            么麽<=>麽麼
            
            """;
}
