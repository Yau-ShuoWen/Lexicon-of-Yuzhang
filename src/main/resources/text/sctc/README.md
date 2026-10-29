# 简繁单字关系

- `opencc-st-characters.txt`：OpenCC `STCharacters.txt`，简体到繁体，一对多关系按原方向保留。原始文件 4,028 行。
- `opencc-ts-characters.txt`：OpenCC `TSCharacters.txt`，繁体到简体，按原方向保留。原始文件 5,061 行。
- `opencc-tw-variants.txt`、`opencc-hk-variants.txt`：OpenCC 台湾、香港地区字形异体表，分别保留。
- `unihan-simplifiedvariant.txt`、`unihan-traditionalvariant.txt`：Unicode Unihan 18.0.0 的简繁对应属性。
- `unihan-zvariant.txt`：Unicode 的同形同义异码关系。
- `unihan-semanticvariant.txt`：Unicode 的同义异体关系。保留供查阅，不直接作为逐字简繁转换或模糊匹配依据。
- `unihan-specializedsemanticvariant.txt`：仅在特定语境下相关，供 `contextRelatedMappings()` 单独读取，不参与正则合并。
- `manual.txt`：原 `ScTcRegex` 中的人工补充关系，左右两侧各可含多个字。

OpenCC 原始数据：https://github.com/BYVoid/OpenCC/tree/master/data/dictionary ，许可为 Apache-2.0：https://github.com/BYVoid/OpenCC/blob/master/LICENSE 。更新时应从同一版本获取完整文件，并保留源文件头部。两个 OpenCC 文件的完整性已按原始行号核对。

Unihan 原始数据：https://www.unicode.org/Public/18.0.0/ucd/Unihan.zip ，从其中的 `Unihan_Variants.txt` 按属性拆分。原始压缩包 SHA-256：`4C93EA9C1F636451729A840978F1667A53886AF37BA854FDCCE109721C63D43E`。授权文本见本目录的 `UNICODE-LICENSE.txt`；OpenCC 授权文本见 `OPENCC-LICENSE.txt`。

`ScTcCharacterData.sourceMappings()` 提供分来源、未合并的只读映射；`ScTcCharacterConverter` 负责一个 Unicode 汉字的有方向转换；`ScTcRegex` 只负责把可互换字形展开成正则。构造模糊匹配集合时，同义异体和仅在特定语境下相关的异体不参与合并。资源随程序打包，在类初始化时用 Java 类路径读取，不需要 Spring 或数据库。修改文件后需重新启动进程以重新加载。

`ScTcCharacterConverter.lookup("里")` 将大陆简体、台湾繁体和其他异体分开返回；按单字表得到的台湾繁体候选包含「裡、里、哩」，其中「裏」列为其他异体。`lookup("说")` 返回大陆简体「说」、台湾繁体「說」、其他异体「説」。`lookup("为")` 返回大陆简体「为」、台湾繁体「為」、其他异体「爲」。一对多的字形无法在没有词语语境时只选一个。词语转换仍由其他工具处理。

以 `GonnyuGeneralIME/resources/regions/lancong/dictionaries/chars.tsv` 第一列为核对目标：23,902 行，去重后 20,500 个 Unicode 单字，其中 4,972 个位于基本多文种平面之外。仅按可互换字形关系计算，5,009 个目标字有非自身对应，其余 15,491 个按原字匹配；没有找到的对应不能凭字形猜造。最大连通分量为 6 字。以上统计应在更新字表或目标字库后重新核对。
