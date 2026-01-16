| 方法名                        | 操作说明                 | 操作对象            | 限制条件（WHERE / 依据） |
|----------------------------|----------------------|-----------------|------------------|
| `findHanziByScTc         ` | 使用简体 / 繁体查找汉字        | xxx_char（主表）    | hanzi（简 / 繁）     |
| `findHanziByVague        ` | 使用简繁体 + 模糊匹配查找汉字     | xxx_char（主表）    | hanzi（LIKE / 模糊） |
| `findHanziByScOrTc       ` | 使用指定的简体或繁体查找汉字       | xxx_char（主表）    | hanzi + lang     |
| `findByUniqueKey         ` | 根据唯一键查询是否存在          | xxx_char（主表）    | 唯一键字段            |
| `getAllChar              ` | 查询全部汉字数据             | xxx_char（主表）    | 无                |
| `findHanziByCharId       ` | 通过主键查询汉字             | xxx_char（主表）    | id（主键）           |
| `findPreviousId          ` | 查询上一条汉字记录的主键         | 主表 ID           | id < 当前 id       |
| `findNextId              ` | 查询下一条汉字记录的主键         | 主表 ID           | id > 当前 id       |
| `findHanziSimilarByCharId` | 查询汉字的相似字             | CharSimilar（从表） | char_id（外键）      |
| `findHanziPinyinByCharId ` | 查询汉字的读音              | CharPinyin（从表）  | char_id（外键）      |

