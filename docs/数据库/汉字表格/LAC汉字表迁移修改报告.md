# LAC 汉字表迁移修改报告

## 1. 修改目的

旧结构以 `lac_char.main_py` 区分同一简繁汉字的不同读音，因此同一组 `sc/tc` 会有多条主表记录。其他读音保存在 `lac_char_pinyin`，普通话映射则通过 `lac_char_mdr.dialect_id` 实际对应到某条旧记录的 `main_py`。

新结构取消“主拼音”概念，同一组 `sc/tc` 只保留一条汉字记录，所有方言拼音按顺序保存在 `pinyin` JSON 数组中。普通话显式映射也保存在具体拼音项中。

本次只迁移 LAC。CED、WUH 等其他方言仍使用旧表和旧代码。


## 2. 新数据库结构

### 2.1 `lac_hanzi`

替代旧 `lac_char`、`lac_char_pinyin` 和 `lac_char_mdr` 的主要功能。

主要字段：

| 字段 | 说明 |
|---|---|
| `id` | 新表自增主键；与旧 `lac_char.id` 不对应 |
| `sc` | 简体汉字，列排序规则为 `utf8mb4_bin` |
| `tc` | 繁体汉字，列排序规则为 `utf8mb4_bin` |
| `pinyin` | 所有拼音、标签、排序码和普通话显式映射组成的 JSON 数组 |
| `special` | 汉字特殊性标记；新增值 `4` 表示迁移冲突、等待人工处理 |
| `note` | 合并后的注释 JSON 数组 |
| `created_at` | 被合并旧记录中最早的创建时间 |
| `updated_at` | 被合并旧记录中最晚的更新时间 |
| `status` | 词条状态 |

唯一约束：

```sql
UNIQUE (sc, tc)
```

`sc/tc` 必须使用二进制排序规则。`utf8mb4_general_ci` 会把部分不同的 Unicode 扩展汉字判断为相同，例如：

```text
𩑃  F0A99183
𭮷  F0ADAEB7
```

因此，后端涉及汉字身份、连接、分组和精确查找时，也应采用二进制比较。

### 2.2 `lac_hanzi_similar`

替代旧 `lac_char_similar`。

主要变化：

- 表名中的 `char` 改为 `hanzi`；
- `char_id` 改为 `hanzi_id`；
- `hanzi_id` 外键指向 `lac_hanzi.id`；
- `sc/tc` 使用 `utf8mb4_bin`；
- 唯一约束为 `(hanzi_id, sc, tc)`。

### 2.3 不再创建的 LAC 表

新结构不再需要：

```text
lac_hanzi_pinyin
lac_hanzi_mdr
```

原 `lac_char_pinyin` 内容已经合并到 `lac_hanzi.pinyin`，原 `lac_char_mdr` 内容已经转换为每个拼音项中的 `mandarin` 数组。


## 3. `pinyin` JSON 格式

最终结构：

```json
[
  {
    "pinyin": "cẹn",
    "code": "035045",
    "tag": {
      "sc": "文读",
      "tc": "文讀"
    },
    "mandarin": [12, 15]
  }
]
```

字段含义：

| 字段 | 说明 |
|---|---|
| `pinyin` | 方言数据库拼音；比较时必须使用二进制方式 |
| `code` | 由现有拼音类 `getWeight()` 生成的排序码 |
| `tag.sc/tag.tc` | 原拼音标签，例如文读、白读、老派、新派 |
| `mandarin` | 显式对应的 `mdr_char.id` 数组，不建立数据库外键 |

数组顺序就是拼音显示顺序和人工排序结果。不再保存单独的 `sort` 字段，也不再存在 `main_py`。

如果业务仍需要一个默认展示或汉字间排序依据，使用数组第一项，但不要再称为“主拼音”。


## 4. 拼音比较规则

拼音必须按照二进制内容精确比较，不能使用 `utf8mb4_general_ci`。例如查询 `cẹn` 时不能同时匹配 `cen`。

MySQL 8 推荐写法：

```sql
CAST(left_value AS BINARY) = CAST(right_value AS BINARY)
```

不要使用已经被 MySQL 标记为弃用的：

```sql
BINARY value
```

从 JSON 查询拼音时可使用：

```sql
SELECT DISTINCT h.*
FROM lac_hanzi h
JOIN JSON_TABLE(
    h.pinyin,
    '$[*]' COLUMNS (
        pinyin VARCHAR(20) CHARACTER SET utf8mb4
            COLLATE utf8mb4_bin PATH '$.pinyin'
    )
) py
WHERE CAST(py.pinyin AS BINARY) = CAST(? AS BINARY);
```


## 5. 普通话映射规则

普通话映射粒度是“具体方言拼音”，不是合并后的整个汉字。

规则：

- 一个方言拼音可以显式对应 `0-N` 个普通话读音；
- 一个普通话读音最多显式对应一个方言拼音；
- 只有方言拼音恰好一个，并且普通话候选也恰好一个时，才不保存显式映射，由运行时自动对应；
- 方言拼音或普通话候选任一侧存在多个选择时，必须读取和保存显式映射；
- `mandarin` 数组保存 `mdr_char.id`，但不是数据库外键；保存时必须由后端检查 ID 是否存在和是否重复占用。

例如“什”有 `xit6`、`sẹt6` 两个方言拼音，即使只有一个普通话候选 `什 shi2`，也必须显式保存该普通话读音属于 `sẹt6`，否则无法确定对应关系。


## 6. 旧数据合并规则

### 6.1 主记录合并

旧表中相同二进制 `sc/tc` 的记录合并成一个 `lac_hanzi`：

- 每条旧记录的 `main_py` 都成为一个拼音项；
- `lac_char_pinyin` 中的其他读音一并加入；
- 同一个汉字内，二进制完全相同的拼音只保留一项；
- 普通话映射通过旧 `lac_char_mdr.dialect_id → lac_char.main_py` 精确挂到对应拼音。

### 6.2 非拼音字段冲突

- `special` 一致时保留原值；不一致时设为 `4`，留待人工处理；
- `status` 一致时保留原值；不一致时设为 `2`（需要补充内容）；
- `note` 按旧记录 ID 和原数组顺序合并，不去重；
- `created_at` 取最早值；
- `updated_at` 取最晚值。

`special` 被标记为 `4` 的汉字来自以下冲突组：

```text
什、别、去、日、渠、滴、的、给、著、行、许、话
```

这些内容需要之后人工检查并改回 `0-3` 中的正确值。

存在状态冲突并被统一设为 `2` 的主要汉字：

```text
日、给、难
```


## 7. 迁移结果

最终校验结果：

| 项目 | 结果 |
|---|---:|
| 新汉字数 | 504 |
| 旧表按二进制 `sc/tc` 去重后的预期汉字数 | 504 |
| 新拼音项数 | 568 |
| 旧表按二进制拼音去重后的预期拼音项数 | 568 |
| 旧表存在但新表缺失的拼音 | 0 |
| 新表额外产生的拼音 | 0 |
| 空拼音或空拼音数组 | 0 |
| 重复占用的普通话 ID | 0 |
| 不存在于 `mdr_char` 的普通话 ID | 0 |
| 空 `code` | 0 |

迁移初期有 37 个附表拼音无法从旧 `main_py` 找到排序码，已经使用 Java 拼音类全量计算并回填：

```text
扫描汉字 504 条
更新汉字 36 条
补全 code 37 个
复查待回填 code 0 个
```


## 8. 相关迁移文件

- SQL 建表、导入和校验脚本：`docs/数据库/汉字表格/lac_hanzi_migration.sql`
- 一次性排序码回填测试：`src/test/java/com/shuowen/yuzong/migration/LacHanziPinyinCodeMigrationTest.java`

排序码回填测试中的开关已经完成使命，后续应保持：

```java
private static final boolean ENABLE_WRITE = false;
```


## 9. 后端改造重点

当前前后端尚未切换，新旧表需要并存。后端改造时至少处理以下内容：

1. LAC 查询从 `lac_char` 切换到 `lac_hanzi`。
2. 不能直接全局替换动态 `${dialect}_char`，因为其他方言尚未迁移。
3. `HanziEntity.mainPy`、`pyCode`、`variantPy` 合并为新的拼音数组模型。
4. 删除 LAC 对 `lac_char_pinyin` 的增删改查。
5. 删除 LAC 对 `lac_char_mdr` 的增删改查和连接查询。
6. 保存汉字时一次性校验并写入整个 `pinyin` JSON。
7. 校验同一汉字内拼音不能二进制重复。
8. 校验 `mandarin` 中每个 ID 存在于 `mdr_char`。
9. 校验同一个普通话 ID 不能显式分配给多个方言拼音。
10. 实现“一对一时自动映射，多选时使用显式映射”的读取逻辑。
11. 以前读取单个 `mainPy` 的展示、搜索、分组代码改为遍历拼音数组。
12. 上一条、下一条排序可暂时读取 `pinyin[0].code`；数据量增大后再考虑生成列和索引。
13. `lac_hanzi.id` 是重新生成的，不能假设与旧 `lac_char.id` 相同。
14. 相似字外键和 Mapper 字段从 `char_id` 改为 `hanzi_id`。

主要受影响文件包括：

```text
src/main/resources/mapper/Character/HanziMapper.xml
src/main/resources/mapper/Character/PronunMapper.xml
src/main/java/com/shuowen/yuzong/dict/data/model/Character/HanziEntity.java
src/main/java/com/shuowen/yuzong/dict/data/domain/Character/HanziUpdate.java
src/main/java/com/shuowen/yuzong/dict/data/domain/Character/HanziCreate.java
src/main/java/com/shuowen/yuzong/dict/data/domain/Character/HanziItem.java
src/main/java/com/shuowen/yuzong/dict/data/domain/Character/HanziGroup.java
src/main/java/com/shuowen/yuzong/dict/data/domain/Character/HanziShow.java
src/main/java/com/shuowen/yuzong/dict/service/Character/HanziService.java
src/main/java/com/shuowen/yuzong/dict/service/Character/PronunService.java
```

不要为了 LAC 修改公共拼音组件的既有行为；优先在 LAC 汉字数据层增加适配。


## 10. 前端改造重点

主要受影响页面：

```text
yuzongweb/src/views/Developer/Hanzi/HanziEditor.vue
```

建议调整：

1. 删除“主拼音”单选区域。
2. `variantPy` 和 `mainPy` 合并为统一的 `pinyin` 数组。
3. 拖动数组直接改变拼音显示顺序。
4. 每条拼音独立编辑标签、拼音和普通话映射。
5. 普通话映射不能继续放在整个汉字层级。
6. 方言拼音和普通话候选都是唯一项时，显示为自动对应，不提交显式映射。
7. 任一侧存在多个选项时，在具体方言拼音下面显示普通话选择控件。
8. 不增加无关英文提示。


## 11. 后续清理限制

在以下事项全部完成前，不要删除旧表：

- 后端完成 LAC 新结构适配；
- 前端编辑和展示完成适配；
- 汉字搜索、详情、编辑、新增、相似字、普通话映射、上下条导航全部验证；
- 日志和其他隐藏引用确认不再使用旧 ID；
- 新结构稳定运行并完成备份。

待清理的旧表最终包括：

```text
lac_char
lac_char_pinyin
lac_char_similar
lac_char_mdr
```

清理必须单独设计 SQL，并在确认前由人工执行。
