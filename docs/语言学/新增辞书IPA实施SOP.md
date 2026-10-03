# 新增辞书 IPA 实施 SOP

这份流程适用于目前的“每部辞书各自实现，统一接口调用”架构。它不是要求每部辞书写成同一个形状；目标是让每部辞书的收录范围、音值规则和例外都能在自己的类里读懂。

## 先决定这是不是一部新的 IPA 辞书

1. 记下辞书全名、方言、语言文字、资料出处，以及它使用的拼音方案。
2. 确认该资料确实给出 IPA。只有辞书身份、没有 IPA 的资料，可以加入方言辞书列表，但不要注册一个返回臆造音值的 IPA 实现。现有 `NCDIALSTD` 就是这种情况。
3. 若拼音方案仍是 `LACPinyin`，按下面的南昌话流程实施。若音节结构本来不同，应另建该方言的辞书边界；不要为了套用 `LACDictionary` 改动拼音结构。

## 第一步：把规则整理清楚，再写 Java

准备一张工作表，至少列出：独立音节、声母、所有韵部、声调，以及明确未收录的项目。韵部最好按 `介母 / 韵母 / 韵尾` 拆开观察，但这只是分析工具，不预设 IPA 必须逐件拼接。

对每个差异问四个问题：

1. 同一个部分在别的部分变化时，音值是否始终不变？若是，它可以成为独立函数，例如稳定的 `i → i`。
2. 音值是否取决于两个或三个部分的组合？若是，把这些部分一起传给本辞书的函数。例如 `a` 随韵尾变音值，就不能只写 `handleYunMu(YunMu)`。
3. 这是可概括的规则，还是少数明确的例外？先写规则，再把真正的例外放在局部 `switch`／条件中。不要把整张资料表手写成大 HashMap。
4. 某个项目是“资料未收录”，还是“收录但音值为空”？前者返回 `Maybe.nothing()`；后者才是合法的空字符串。声调缺失也不要等同于轻声 `0`。

先选十几个有区分力的例子：零声母、普通声母、`i/u/ü` 介母、鼻尾、入声尾、独立音节、每个特殊组合和每个不收录项目。用这些例子验证规则能否解释原始资料。

## 第二步：选继承层级

| 情况 | 做法 |
| --- | --- |
| 新方言或音节结构不同 | 实现 `PronunciationDictionary`，另建该方言的类型边界 |
| 南昌话辞书，声母＋韵部＋声调流程适用 | 继承 `LACZuheCidian` |
| 南昌话辞书，但整体流程也不同 | 继承 `LACDictionary`，自行实现 `transcribe` |

`PronunciationDictionary` 只统一 `code()`、`dialect()`、`transcribe(...)` 这个调用接口。`LACDictionary` 只提供南昌话类型检查和现有标准映射；它**没有**通用的 `handleYunMu`。`LACZuheCidian` 只统一“判断收录 → 生成音节 → 加声调”的流程，韵部怎样组成仍由辞书决定。

父类里的 `handleDandu`、`handleShengMu`、`handleJieMu`、`handleYunWei` 是现有标准写法。只有新辞书也确实采用该写法时才继承；少数不同的项目在本辞书覆写并对其他项调用 `super`。不要为了新辞书修改这些公用函数的行为，除非先确认所有调用者并征得同意。

## 第三步：建辞书身份与实现类

1. 在 `DictCode` 加代号、可读书名、`sc`／`tc`、所属方言，以及辞书身份：独立辞书把 `completeDictionary` 设为 `true`；派生记音设为 `false` 并填写中心辞书；内部默认方案设为 `fallback=true`，但不算独立辞书。每个方言只能有一个兜底方案。代号供程序使用；类名用完整拼音，如 `Dict_NanchanghuaYindang`，不要用看不懂的短英文缩写。
2. 在 `linguistics/pinyinv2/lac/` 新建该辞书的类。`LACZuheCidian` 是包内类，南昌话实现应放在同一包。
3. 先实现 `code()` 和 `supports(...)`，再写音值。`supports` 只回答本辞书是否收录某韵部，不负责生成 IPA。只有经过资料核对、收录范围确实相同，才复用别的辞书的 `supports`；“音值相同”不代表“收录范围相同”。

最小骨架如下，`NEWDICT` 和规则都需要换成真实内容：

```java
package com.shuowen.yuzong.linguistics.pinyinv2.lac;

import com.shuowen.yuzong.linguistics.pinyinv2.DictCode;
import com.shuowen.yuzong.util.tuple.Maybe;

public final class Dict_ShiLiCidian extends LACZuheCidian
{
    public static final Dict_ShiLiCidian INSTANCE = new Dict_ShiLiCidian();

    private Dict_ShiLiCidian() {}

    @Override
    public DictCode code()
    {
        return DictCode.NEWDICT;
    }

    @Override
    protected boolean supports(LACPinyin.YinJie y)
    {
        return switch (y.getYun())
        {
            // 只列本辞书实际收录的韵部。
            case "a", "an" -> true;
            default -> false;
        };
    }

    @Override
    protected String handleYun(LACPinyin.YinJie y)
    {
        // 在这里按本辞书规则处理介母、韵母、韵尾的独立项或组合项。
        return handleJieMu(y.getJiemu()) +
                handleYunMu(y.getYunmu(), y.getYunwei()) +
                handleYunWei(y.getYunwei());
    }

    private String handleYunMu(LACPinyin.YunMu m, LACPinyin.YunWei w)
    {
        return switch (m)
        {
            // 以下只是演示“音值依赖韵尾”的写法，不是新辞书的真实规则。
            case a -> w == LACPinyin.YunWei.n ? "æ" : "a";
            default -> throw new IllegalStateException("请补齐本辞书的韵母规则：" + m);
        };
    }

    @Override
    protected Maybe<String> toneValue(LACPinyin.YinDiao tone)
    {
        return switch (tone)
        {
            // 按资料填入全部实际收录的声调；未收录的调返回 nothing()。
            case t0 -> Maybe.exist("0");
            default -> Maybe.nothing();
        };
    }
}
```

不要机械套用示例中的 `handleYunMu`：若介母也影响韵母，就改成接收 `JieMu/YunMu/YunWei`；若整个韵部才是最小规则单位，就在 `handleYun(YinJie)` 中整体处理。`handleYun` 的非空返回值只应覆盖 `supports` 接受的项目。

## 第四步：接入与格式

在 `DictionaryRegistry` 的 `IPA_DICTIONARIES` 中注册代号与 `INSTANCE`。方言归属、完整辞书列表、派生版本和默认兜底都由 `DictCode` 元数据生成，不再维护另一张手写列表。现有 `LACIPA.query` 会通过注册表调用实现，不需要增加新的 `if (dict == ...)` 分支。

`LACZuheCidian` 已通过 `IPAFormatter.formatSyllable` 处理 `CHINESE_SPECIAL`／`STANDARD_IPA`，并通过 `formatTone` 处理三种声调显示格式。辞书通常只需提供原始音值和调值；若资料的某个调在格式化后仍有特殊写法，应只在本辞书覆写相应流程，不能为单部辞书改动全局 `IPAFormatter`。

## 第五步：验证，之后再整理代码

1. 逐项比较原始资料中的每个独立音节、声母、韵部和声调；不能只抽几个常见音节。组合规则至少测每个影响条件的两侧，例如 `a` 配普通韵尾和特殊韵尾。
2. 分别测试已收录与未收录：已收录值必须精确一致，未收录必须是 `Maybe.nothing()`，不能输出看似合理但资料没有的 IPA。
3. 检查零声母和普通声母组合、无声调与 `0` 调，以及 `CHINESE_SPECIAL`／`STANDARD_IPA` 和三种 `IPAToneStyle`。
4. 检查 `DictionaryRegistry.findIpaDictionary(code)` 和 `LACIPA.query(pinyin, code)` 的结果一致；同一条目在其他辞书下的输出不得因新实现改变。
5. 编译检查。若本机 Maven 被默认仓库权限挡住，做静态 Java 检查后记录限制即可，不要反复重试。若写了临时后端测试代码，验证后删除。
6. 最后再清理重复分支；只有确定旧路径没有调用者且结果已比对，才移除旧实现或数据文件。保留可追溯的原始资料出处。

完成标准：新辞书的收录范围和音值均由它自己的实现解释；注册表能找到它；格式输出正确；未收录项不会被错误拼接；没有为了它改变其他辞书或公用函数的行为。
