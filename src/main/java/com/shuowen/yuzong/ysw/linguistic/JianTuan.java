package com.shuowen.yuzong.ysw.linguistic;

import com.shuowen.yuzong.linguistics.Mandarin.HanPinyin;
import com.shuowen.yuzong.util.core.Language;
import com.shuowen.yuzong.util.text.ProofreadTool;
import com.shuowen.yuzong.util.text.RichTextTable;

import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;

public class JianTuan
{
    private static final String DATA = """
            团音ji 几饥肌虮机讥玑矶麂已记纪忌跽奇寄剞畸骑基箕幾畿吉佶姞髻技伎妓既暨季悸冀骥及级岌芨汲圾屐亟极洎棘稽垍殛继急计激姬蓟击戟觊筓偈给乩鸡
            尖音zï 积蹟绩渍碛勣脊嵴瘠膌蹐塉鶺济挤剂跻穧荠嚌霁鲚齑齊齌齍齎即鲫唧耤踖藉籍跡迹疾蒺嫉祭际穄寂潗集赍稷辑楫戢濈
            团音qi 期欺旗棋麒琪其奇骑欹琦蕲屺起杞乞汽讫岐芪祈颀启契气弃祁憩亓企器岂泣歧耆鳍
            尖音cï 七柒沏砌齐懠脐蛴妻棲萋凄悽郪戚磩嘁槭缉葺咠刺桼漆
            团音xi 希稀俙欷唏晞烯豨喜嘻嬉僖熹禧蟢奚溪傒蹊鼷系羲牺曦爔兮盻吸戏翕歙檄熙携觹畦虩醯阋屃赩饩隙觋綌
            尖音sï 西怬栖茜粞硒氥舾析皙晰蜥淅菥昔惜腊夕汐矽穸徙屣蹝縰蓰息熄媳裼锡瘜緆磶舄潟悉蟋窸犀樨枲洗铣细葸习霫席蓆膝袭纚隰玺卌
            团音jian 见舰俭捡硷睑检剑间简锏涧建健键犍兼碱减柬拣谏坚监鉴豜奸菅件艰蹇肩茧囝
            尖音zian 箭剪煎谫揃湔翦揃鬋戋笺浅贱饯践諓牋溅籛洊荐僭熸薦鞯戬尖歼櫼渐镌牮熸
            团音qian 牵縴谦歉鹣搴褰攓骞钳箝嵌拑欠芡肷遣谴钤黔汧岍愆虔乾孯铅掮虔悭鹐
            尖音cian 千芊阡仟迁钎扦褼蹮韆浅钱僉签倩蒨綪前媊茜潜籖堑灊荨槧
            团音xian 掀锨閒娴痫鹇闲现苋睍峴晛蜆咸弦舷陷馅糮衔啣嗛嫌险猃限贤显献鶱县宪祆忺
            尖音sian 先洗烍跣铣毨酰鲜癣藓仙氙籼線线纤燹涎羡暹挦铦獮霰腺跹筅祆鱻
            团音jiang 畺薑僵缰疆礓绛降江豇讲姜耩犟糨弶
            尖音ziang 浆桨将奖蒋酱螀鳉匠虹
            团音qiang 羌蜣羟强镪襁彊腔矼
            尖音ciang 枪玱呛抢炝戗鎗跄墙樯嫱艢蔷锵蹡爿戕斨锖将
            团音xiang 乡芗蠁飨响響曏香巷向饷享项降缿
            尖音siang 相湘厢箱缃葙想襄镶勷骧纕瓖庠详祥翔象像橡蟓鲞
            团音jiao 交郊鮫蛟皎校饺较绞狡姣娇骄轿敫缴徼教酵浇窖胶搅侥角艽觉藠叫
            尖音ziao 焦醮噍燋蕉僬礁蟭鹪醮勦椒灂嚼湫
            团音qiao 乔桥荞侨峤鞒跷翘硗橇撬巧窍敲荍骹
            尖音ciao 瞧樵憔劁谯俏悄诮睄峭鞘愀锹雀繰幧
            团音xiao 哓骁晓效効校恔傚枵鴞孝哮烋枭崤淆囂歊熇虓斅猇
            尖音siao 肖逍消宵霄销硝箾绡蛸魈箫萧潇啸歗蠨筱翛笑謏小蠨謏
            团音jie 结拮桔诘洁颉劼鲒杰傑揭碣竭偈羯楬劫蜐讦孑解
            尖音zie 节疖櫛擳蠞倢捷睫媫截巀姐借唶藉毑接褯
            团音qie 挈锲茄伽惬箧怯慊
            尖音cie 窃砌切且趄妾
            团音xie 胁协勰歇蝎猲颉撷挟叶絜鞋血解邂獬蟹懈械偕谐携叶绁瀣亵勰
            尖音sie 舄写泻洩泄紲緤渫屟谢榭契褉揳楔衺亵燮躞邪斜屑些卸卨灺
            团音jin 今衿矜妗堇谨仅斳槿馑殣觐瑾劲禁襟噤金唫锦筋斤近靳巾紧巹
            尖音zin 津浸祲尽侭荩浕烬赆晋搢缙瑨进琎
            团音qin 钦嵚矜捦衾琴芩禽擒噙檎勤懃厪芹
            尖音cin 亲侵寝骎祲綅锓秦嗪溱螓沁吣
            团音xin 欣忻訢昕炘焮歆釁衅鑫馨鐔
            尖音sin 辛新薪莘锌骍侁诜駪汛讯迅心芯信囟寻
            团音jing 京景倞璟憬镜经劲颈泾刭径迳胫痉茎弪敬儆警惊竟境獍兢竞荆粳
            尖音zing 精靖睛静菁箐蜻鶄腈婧鼱井穽阱汫肼阱净旌晶
            团音qing 氢轻檠擎罄謦磬綮勍剠黥鲸顷倾庆卿苘庼
            尖音cing 青凊清情请晴倩鲭靘氰蜻圊[贝青]箐亲
            团音xing 兴幸倖悻涬婞形刑型侀硎鉶邢行荇杏陉娙荥鈃
            尖音sing 星醒腥惺猩鯹姓性饧省箵擤骍
            团音jiu 九氿究鸠久玖疚柩灸臼桕舅旧救纠赳咎韭阄厩
            尖音ziu 酒就僦鹫揪蹴啾鬏
            团音qiu 丘邱蚯求裘毬俅捄球絿賕銶逑仇虬璆糗巯犰訄鼽龟
            尖音ciu 秋啾萩楸鞦鶖鳅湫酋遒崷蝤鞧囚泅
            团音xiu 休咻庥貅髹鸺臭嗅溴齅糗朽
            尖音siu 羞馐脩滫修绣岫袖秀锈璓宿褏
            团音jü 巨苣矩拒炬距居锯倨琚句拘枸佝局驹跼據遽匊掬鞠菊莒筥橘具俱飓犋举车惧
            尖音zü 聚狙疽且雎咀苴沮龃趄咀龃罝砠娵驺
            团音qü 区躯驱岖去袪呿胠瞿衢癯欋蠷鸜渠璩蘧籧劬朐鴝鼩紶曲麯蛐屈龋
            尖音cü 蛆趋趣取娶觑狙焌黢蹴
            团音xü 虚嘘歔墟驉畜蓄吁盱诩栩煦姁呴旭勗顼许洫侐砉酗魆
            尖音sü 须鬚嬃盨胥湑壻婿醑糈谞稰偦恤卹叙溆序垿绪絮续俆溆需繻戌宿蓿
            团音jüan 涓绢捐娟悁睊狷鹃罥卷倦捲眷桊锩圈帣蠲鄄
            尖音züan 镌隽朘
            团音qüan 权劝颧踡蜷棬捲惓券拳圈犬畎弮酄
            尖音cüan 全痊佺荃诠铨筌跧牷醛辁泉悛鳈
            团音xüan 轩玄袨炫泫铉昡眩痃衒暄煊喧萱楦儇塇晅嬛翾谖烜悬绚券
            尖音süan 宣瑄揎渲喧旋漩璇鏇选璿
            团音jüe 决诀抉玦觖趹鴃厥蕨噘镢蹶劂矍钁攫玃角桷珏脚觉獗橛橛掘倔譎鐍噱孓
            尖音züe 绝爵嚼皭爝罝嗟灂
            团音qüe 却确炔缺瘸阙阕榷悫
            尖音cüe 雀鹊碏
            团音xüe 靴学穴泬血峃
            尖音süe 薛削雪鳕踅茓
            团音jün 郡君捃均钧筠菌麕麏麇军箘龟
            尖音zün 俊峻骏逡浚畯竣馂鵔焌鵕儁隽寯濬
            团音qün 裙群峮宭囷麇
            尖音cün 夋逡踆皴
            团音xün 训熏薰曛纁臐埙勋勲醺燻獯蔒荤窨
            尖音sün 迅汛讯旬询峋恂洵郇殉荀狥徇栒寻浔鲟璕桪噚燖蟳紃巡逊巽噀循蕈驯
            """;

    private static final Pattern BRACKET_NOTE = Pattern.compile("\\[[^]]*]");
    private static final Pattern PARENTHESIS_NOTE = Pattern.compile("（[^）]*）|\\([^)]*\\)");
    private static final Pattern DATA_LINE = Pattern.compile("^(尖音|团音)(\\S+)\\s+(.+)$");
    private static final Set<String> JIAN_CHARS;
    private static final Set<String> TUAN_CHARS;
    private static final Map<String, Set<String>> JIAN_PINYIN;
    private static final Map<String, Set<String>> TUAN_PINYIN;
    private static final Map<Character, String[]> TONE_MARKS = Map.of(
            'a', new String[]{"a", "ā", "á", "ǎ", "à"},
            'o', new String[]{"o", "ō", "ó", "ǒ", "ò"},
            'e', new String[]{"e", "ē", "é", "ě", "è"},
            'i', new String[]{"i", "ī", "í", "ǐ", "ì"},
            'ï', new String[]{"ï", "ï\u0304", "ï\u0301", "ï\u030C", "ï\u0300"},
            'u', new String[]{"u", "ū", "ú", "ǔ", "ù"},
            'ü', new String[]{"ü", "ǖ", "ǘ", "ǚ", "ǜ"}
    );

    static
    {
        Set<String> jianChars = new LinkedHashSet<>();
        Set<String> tuanChars = new LinkedHashSet<>();
        Map<String, Set<String>> jianPinyin = new LinkedHashMap<>();
        Map<String, Set<String>> tuanPinyin = new LinkedHashMap<>();

        for (String rawLine : DATA.lines().toList())
        {
            String line = rawLine.trim();
            if (line.isEmpty()) continue;

            var matcher = DATA_LINE.matcher(line);
            if (!matcher.matches()) throw new IllegalStateException("尖团音字表格式错误：" + line);

            Set<String> target = switch (matcher.group(1))
                    {
                        case "尖音" -> jianChars;
                        case "团音" -> tuanChars;
                        default -> throw new IllegalStateException("尖团音类别错误：" + matcher.group(1));
                    };
            Map<String, Set<String>> targetPinyin = "尖音".equals(matcher.group(1)) ? jianPinyin : tuanPinyin;
            String tablePinyin = matcher.group(2);

            String chars = BRACKET_NOTE.matcher(matcher.group(3)).replaceAll("");
            chars = PARENTHESIS_NOTE.matcher(chars).replaceAll("");
            chars = toSimplified(chars);

            chars.codePoints()
                    .filter(JianTuan::isHanCharacter)
                    .mapToObj(JianTuan::fromCodePoint)
                    .forEach(ch ->
                    {
                        target.add(ch);
                        targetPinyin.computeIfAbsent(ch, ignored -> new LinkedHashSet<>()).add(tablePinyin);
                    });
        }

        JIAN_CHARS = Collections.unmodifiableSet(jianChars);
        TUAN_CHARS = Collections.unmodifiableSet(tuanChars);
        JIAN_PINYIN = unmodifiableReadings(jianPinyin);
        TUAN_PINYIN = unmodifiableReadings(tuanPinyin);
    }

    private JianTuan()
    {
    }

    /**
     * 按字判断尖团音，返回前端富文本支持的表格格式。
     */
    public static String format(String input)
    {
        if (input == null || input.isBlank()) return "";

        RichTextTable table = new RichTextTable()
                .addColumn("汉字", RichTextTable.Alignment.CENTER, RichTextTable.Width.NOWRAP)
                .addColumn("尖音", RichTextTable.Alignment.CENTER, RichTextTable.Width.NOWRAP)
                .addColumn("团音", RichTextTable.Alignment.CENTER, RichTextTable.Width.NOWRAP)
                .addColumn("其他", RichTextTable.Alignment.CENTER, RichTextTable.Width.NOWRAP)
                .addColumn("拼音", RichTextTable.Alignment.LEFT, RichTextTable.Width.NORMAL);

        toSimplified(input).codePoints()
                .filter(JianTuan::isHanCharacter)
                .mapToObj(JianTuan::fromCodePoint)
                .forEach(ch -> appendRow(table, ch));

        return table.toString();
    }

    private static void appendRow(RichTextTable table, String ch)
    {
        boolean isJian = JIAN_CHARS.contains(ch);
        boolean isTuan = TUAN_CHARS.contains(ch);
        PinyinInfo pinyin = (isJian || isTuan) ? getTablePinyin(ch) : getMandarinPinyin(ch);

        String jianMark = isJian ? "✅" : "";
        String tuanMark = isTuan ? "✅" : "";
        String otherMark = "";

        if (!isJian && !isTuan)
        {
            if (pinyin.fixedTuan())
            {
                tuanMark = "✅";
            }
            else if (pinyin.startsWithJqx())
            {
                jianMark = "❓";
                tuanMark = "❓";
            }
            else
            {
                otherMark = "✅";
            }
        }

        table.addRow(ch, jianMark, tuanMark, otherMark, pinyin.display());
    }

    private static PinyinInfo getTablePinyin(String ch)
    {
        Set<String> reads = new LinkedHashSet<>();
        Set<String> tablePinyins = new LinkedHashSet<>();
        tablePinyins.addAll(JIAN_PINYIN.getOrDefault(ch, Set.of()));
        tablePinyins.addAll(TUAN_PINYIN.getOrDefault(ch, Set.of()));

        List<HanPinyin> mandarinPinyins = getHanPinyins(ch);
        for (String tablePinyin : tablePinyins)
        {
            boolean matched = false;
            for (HanPinyin mandarinPinyin : mandarinPinyins)
            {
                if (!isSameSyllable(tablePinyin, mandarinPinyin.getSyll())) continue;

                reads.add(toRichPinyin(markTone(tablePinyin, mandarinPinyin.getTone())));
                matched = true;
            }
            if (!matched) reads.add(toRichPinyin(tablePinyin));
        }
        return new PinyinInfo(new ArrayList<>(reads), false, false);
    }

    private static PinyinInfo getMandarinPinyin(String ch)
    {
        try
        {
            List<HanPinyin> pinyins = getHanPinyins(ch);
            Set<String> reads = new LinkedHashSet<>();
            boolean startsWithJqx = false;
            boolean fixedTuan = false;

            for (HanPinyin pinyin : pinyins)
            {
                reads.add(pinyin.getRead().toString().trim());
                String syllable = pinyin.getSyll();
                if (!syllable.isEmpty() && "jqx".indexOf(syllable.charAt(0)) >= 0)
                {
                    startsWithJqx = true;
                }
                if (Set.of("jia", "qia", "xia").contains(syllable)) fixedTuan = true;
            }

            return new PinyinInfo(new ArrayList<>(reads), startsWithJqx, fixedTuan);
        }
        catch (RuntimeException ignored)
        {
            return new PinyinInfo(List.of(), false, false);
        }
    }

    private static List<HanPinyin> getHanPinyins(String ch)
    {
        try
        {
            return HanPinyin.toPinyinList(ch);
        }
        catch (RuntimeException ignored)
        {
            return List.of();
        }
    }

    private static boolean isSameSyllable(String tablePinyin, String mandarinPinyin)
    {
        return normalizeSyllable(tablePinyin, true).equals(normalizeSyllable(mandarinPinyin, false));
    }

    private static String normalizeSyllable(String syllable, boolean tablePinyin)
    {
        String normalized = syllable.toLowerCase(Locale.ROOT);
        if (tablePinyin)
        {
            normalized = switch (normalized)
                    {
                        case "zï" -> "ji";
                        case "cï" -> "qi";
                        case "sï" -> "xi";
                        default -> normalized;
                    };
        }
        normalized = normalized.replace('ï', 'i').replace('ü', 'u').replace('v', 'u');
        if (!tablePinyin || normalized.isEmpty()) return normalized;

        return switch (normalized.charAt(0))
                {
                    case 'z' -> 'j' + normalized.substring(1);
                    case 'c' -> 'q' + normalized.substring(1);
                    case 's' -> 'x' + normalized.substring(1);
                    default -> normalized;
                };
    }

    private static String markTone(String syllable, int tone)
    {
        if (tone < 1 || tone > 4) return syllable;

        int vowelIndex = syllable.contains("iu") ? syllable.indexOf("iu") + 1 : -1;
        if (vowelIndex < 0)
        {
            for (char vowel : "aoeiïuü".toCharArray())
            {
                vowelIndex = syllable.indexOf(vowel);
                if (vowelIndex >= 0) break;
            }
        }
        if (vowelIndex < 0) return syllable;

        char vowel = syllable.charAt(vowelIndex);
        String marked = TONE_MARKS.get(vowel)[tone];
        String result = syllable.substring(0, vowelIndex) + marked + syllable.substring(vowelIndex + 1);
        return Normalizer.normalize(result, Normalizer.Form.NFC);
    }

    private static String toRichPinyin(String pinyin)
    {
        return '[' + pinyin + ']';
    }

    private static String toSimplified(String text)
    {
        return ProofreadTool.useHanlpTranslate(text, Language.TC);
    }

    private static Map<String, Set<String>> unmodifiableReadings(Map<String, Set<String>> source)
    {
        Map<String, Set<String>> result = new LinkedHashMap<>();
        source.forEach((ch, readings) -> result.put(ch, Collections.unmodifiableSet(readings)));
        return Collections.unmodifiableMap(result);
    }

    private static boolean isHanCharacter(int codePoint)
    {
        return Character.UnicodeScript.of(codePoint) == Character.UnicodeScript.HAN;
    }

    private static String fromCodePoint(int codePoint)
    {
        return new String(Character.toChars(codePoint));
    }

    private record PinyinInfo(List<String> reads, boolean startsWithJqx, boolean fixedTuan)
    {
        private String display()
        {
            return reads.isEmpty() ? "—" : String.join("、", reads);
        }
    }

}
