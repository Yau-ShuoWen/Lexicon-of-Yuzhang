/*
 * LAC 汉字表第一阶段迁移脚本（仅创建新结构并导入数据）
 *
 * 适用：MySQL 8.0+
 * 旧表：lac_char / lac_char_pinyin / lac_char_similar / lac_char_mdr
 * 新表：lac_hanzi / lac_hanzi_similar
 *
 * 设计：
 * 1. 相同 (sc, tc) 的多条 lac_char 合并为一条 lac_hanzi。
 * 2. 旧记录的 main_py 是一个独立读音，也是旧普通话映射所对应的方言读音。
 * 3. pinyin 数组顺序先按旧读音的最小 sort，再按 code、旧主键排列。
 *    无法从旧 main_py 找到 code 的附表读音保留为 code: null，之后由 Java 迁移程序调用 getWeight() 回填。
 * 4. 仅当方言拼音和普通话候选都恰好一个时不保存显式映射；任一侧有多个时均保留映射。
 * 5. 拼音相等判断全部使用 utf8mb4_bin，避免 cẹn 与 cen 等被视为相同。
 * 6. 同一汉字的 special 冲突时写入 4，留待人工处理。
 * 7. 同一汉字的 status 冲突时写入 2（需要补充内容）。
 * 8. 同一汉字的 note 按旧主键和原数组顺序合并。
 * 9. 不删除、不改名、不更新任何旧表。
 *
 * pinyin JSON 示例：
 * [
 *   {
 *     "pinyin": "cẹn",
 *     "code": "...",
 *     "tag": {"sc": "文读", "tc": "文讀"},
 *     "mandarin": [12, 15]
 *   }
 * ]
 */

USE NC;

/* GROUP_CONCAT 用于生成有稳定顺序的 JSON 数组，迁移会话内提高长度上限。 */
SET SESSION group_concat_max_len = 16 * 1024 * 1024;


/* ========================================================================== */
/* 0. 迁移前检查                                                              */
/* ========================================================================== */

/* 0.1 同一汉字的非拼音字段是否不一致。
 * 这些行不会阻止后续 SQL 执行，但必须先人工确认。
 * 正式导入规则：special 冲突写 4；status 冲突写 2；note 列表全部合并。
 */
SELECT
    sc,
    tc,
    COUNT(*) AS old_row_count,
    COUNT(DISTINCT special) AS special_versions,
    COUNT(DISTINCT COALESCE(CAST(note AS CHAR), 'null')) AS note_versions,
    COUNT(DISTINCT status) AS status_versions,
    GROUP_CONCAT(id ORDER BY id) AS old_ids
FROM lac_char
GROUP BY sc, tc
HAVING special_versions > 1
    OR note_versions > 1
    OR status_versions > 1;

/* 0.1.1 对上一个结果逐行展开。
 * 需要结合 main_py 判断 special/note/status 的差异究竟是脏数据，
 * 还是原本就属于某个具体读音。确认前不要执行第 3 节导入。
 */
WITH conflict_hanzi AS
(
    SELECT sc, tc
    FROM lac_char
    GROUP BY sc, tc
    HAVING COUNT(DISTINCT special) > 1
        OR COUNT(DISTINCT COALESCE(CAST(note AS CHAR), 'null')) > 1
        OR COUNT(DISTINCT status) > 1
)
SELECT
    c.sc,
    c.tc,
    c.id AS old_id,
    c.main_py,
    c.py_code,
    c.special,
    c.status,
    c.note,
    c.created_at,
    c.updated_at
FROM lac_char c
JOIN conflict_hanzi conflict
  ON CAST(conflict.sc AS BINARY) = CAST(c.sc AS BINARY)
 AND CAST(conflict.tc AS BINARY) = CAST(c.tc AS BINARY)
ORDER BY c.sc, c.tc, c.id;

/* 0.1.2 同时展开冲突汉字的读音标签和普通话映射，辅助判断差异归属。 */
WITH conflict_hanzi AS
(
    SELECT sc, tc
    FROM lac_char
    GROUP BY sc, tc
    HAVING COUNT(DISTINCT special) > 1
        OR COUNT(DISTINCT COALESCE(CAST(note AS CHAR), 'null')) > 1
        OR COUNT(DISTINCT status) > 1
)
SELECT
    c.sc,
    c.tc,
    c.id AS old_id,
    c.main_py,
    cp.id AS old_pinyin_id,
    cp.pinyin,
    cp.sort,
    cp.sc AS tag_sc,
    cp.tc AS tag_tc,
    map.mandarin_id,
    m.info AS mandarin_info
FROM lac_char c
JOIN conflict_hanzi conflict
  ON CAST(conflict.sc AS BINARY) = CAST(c.sc AS BINARY)
 AND CAST(conflict.tc AS BINARY) = CAST(c.tc AS BINARY)
LEFT JOIN lac_char_pinyin cp ON cp.char_id = c.id
LEFT JOIN lac_char_mdr map ON map.dialect_id = c.id
LEFT JOIN mdr_char m ON m.id = map.mandarin_id
ORDER BY c.sc, c.tc, c.id, cp.sort, cp.id, map.mandarin_id;

/* 0.2 附表拼音找不到对应 main_py 的记录。
 * 这类读音没有可靠的 py_code；脚本仍会导入，但 code 会是 null。
 */
SELECT
    cp.id AS pinyin_row_id,
    cp.char_id,
    c.sc,
    c.tc,
    cp.pinyin
FROM lac_char_pinyin cp
JOIN lac_char c ON c.id = cp.char_id
LEFT JOIN lac_char code_source
       ON CAST(code_source.sc AS BINARY) = CAST(c.sc AS BINARY)
      AND CAST(code_source.tc AS BINARY) = CAST(c.tc AS BINARY)
      AND code_source.main_py COLLATE utf8mb4_bin = cp.pinyin COLLATE utf8mb4_bin
WHERE code_source.id IS NULL
ORDER BY c.sc, c.tc, cp.sort, cp.id;

/* 0.3 同一汉字、同一二进制拼音存在不同标签。脚本会优先采用：
 *     a. 拼音等于所属旧记录 main_py 的标签；
 *     b. sort 最小的标签；
 *     c. 附表 id 最小的标签。
 */
SELECT
    c.sc,
    c.tc,
    ANY_VALUE(cp.pinyin) AS pinyin,
    COUNT(DISTINCT CONCAT(COALESCE(cp.sc, ''), '\0', COALESCE(cp.tc, ''))) AS tag_versions,
    GROUP_CONCAT(
        DISTINCT CONCAT(COALESCE(cp.sc, 'null'), '/', COALESCE(cp.tc, 'null'))
        ORDER BY cp.sort, cp.id
    ) AS tags
FROM lac_char_pinyin cp
JOIN lac_char c ON c.id = cp.char_id
GROUP BY c.sc, c.tc, cp.pinyin COLLATE utf8mb4_bin
HAVING tag_versions > 1;


/* ========================================================================== */
/* 1. 创建新结构                                                              */
/* ========================================================================== */

CREATE TABLE lac_hanzi
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    sc         VARCHAR(10) COLLATE utf8mb4_bin     NOT NULL,
    tc         VARCHAR(10) COLLATE utf8mb4_bin     NOT NULL,
    pinyin     JSON                                NOT NULL,
    special    INT                                 NOT NULL COMMENT '0普通；1特殊方言字；2占位字；3不使用；4迁移冲突待人工处理',
    note       JSON                                NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
    status     INT                                 NOT NULL COMMENT '词条状态，含义沿用旧代码',

    CONSTRAINT uk_lac_hanzi_sc_tc UNIQUE (sc, tc),
    CONSTRAINT ck_lac_hanzi_pinyin_array CHECK (JSON_TYPE(pinyin) = 'ARRAY'),
    CONSTRAINT ck_lac_hanzi_note_array CHECK (JSON_TYPE(note) = 'ARRAY'),
    CONSTRAINT ck_lac_hanzi_special CHECK (special BETWEEN 0 AND 4)
)
COLLATE = utf8mb4_general_ci;

CREATE TABLE lac_hanzi_similar
(
    id       INT AUTO_INCREMENT PRIMARY KEY,
    hanzi_id INT         NOT NULL,
    sc       VARCHAR(20) COLLATE utf8mb4_bin NOT NULL,
    tc       VARCHAR(20) COLLATE utf8mb4_bin NOT NULL,

    CONSTRAINT uk_lac_hanzi_similar UNIQUE (hanzi_id, sc, tc),
    CONSTRAINT fk_lac_hanzi_similar_hanzi
        FOREIGN KEY (hanzi_id) REFERENCES lac_hanzi (id)
)
COLLATE = utf8mb4_general_ci;


/* ========================================================================== */
/* 2. 准备迁移中间数据                                                        */
/* ========================================================================== */

/* 临时表只存在于当前连接；加入清理语句便于修正数据后重复执行本节。 */
DROP TEMPORARY TABLE IF EXISTS tmp_lac_explicit_mandarin;
DROP TEMPORARY TABLE IF EXISTS tmp_lac_mandarin_candidate_count;
DROP TEMPORARY TABLE IF EXISTS tmp_lac_pinyin;
DROP TEMPORARY TABLE IF EXISTS tmp_lac_pinyin_source;
DROP TEMPORARY TABLE IF EXISTS tmp_lac_hanzi_base;
DROP TEMPORARY TABLE IF EXISTS tmp_lac_merged_note;
DROP TEMPORARY TABLE IF EXISTS tmp_lac_note_item;

/* 展开全部 note 数组。FOR ORDINALITY 保留每条旧记录内部的原始顺序。 */
CREATE TEMPORARY TABLE tmp_lac_note_item AS
SELECT
    c.sc,
    c.tc,
    c.id AS old_id,
    expanded.note_order,
    expanded.note_item
FROM lac_char c
JOIN JSON_TABLE(
    COALESCE(c.note, JSON_ARRAY()),
    '$[*]' COLUMNS (
        note_order FOR ORDINALITY,
        note_item JSON PATH '$'
    )
) expanded;

/* 使用窗口版 JSON_ARRAYAGG 合并说明，不受 GROUP_CONCAT 长度上限影响。 */
CREATE TEMPORARY TABLE tmp_lac_merged_note AS
SELECT sc, tc, merged_note
FROM
(
    SELECT
        note_item.sc,
        note_item.tc,
        JSON_ARRAYAGG(note_item.note_item) OVER (
            PARTITION BY CAST(note_item.sc AS BINARY), CAST(note_item.tc AS BINARY)
            ORDER BY note_item.old_id, note_item.note_order
            ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING
        ) AS merged_note,
        ROW_NUMBER() OVER (
            PARTITION BY CAST(note_item.sc AS BINARY), CAST(note_item.tc AS BINARY)
            ORDER BY note_item.old_id DESC, note_item.note_order DESC
        ) AS reverse_row_number
    FROM tmp_lac_note_item note_item
) ranked_note
WHERE reverse_row_number = 1;

/* 每组 (sc, tc) 合并非拼音字段。
 * note 依次按旧主键、旧数组位置拼接；不额外去重，以免误删内容相同但语义独立的说明。
 */
CREATE TEMPORARY TABLE tmp_lac_hanzi_base AS
SELECT
    MIN(c.id) AS representative_old_id,
    ANY_VALUE(c.sc) AS sc,
    ANY_VALUE(c.tc) AS tc,
    CASE
        WHEN COUNT(DISTINCT c.special) > 1 THEN 4
        ELSE MAX(c.special)
    END AS special,
    COALESCE(ANY_VALUE(merged_note.merged_note), JSON_ARRAY()) AS note,
    MIN(c.created_at) AS created_at,
    MAX(c.updated_at) AS updated_at,
    CASE
        WHEN COUNT(DISTINCT c.status) > 1 THEN 2
        ELSE MAX(c.status)
    END AS status
FROM lac_char c
LEFT JOIN tmp_lac_merged_note merged_note
  ON CAST(merged_note.sc AS BINARY) = CAST(c.sc AS BINARY)
 AND CAST(merged_note.tc AS BINARY) = CAST(c.tc AS BINARY)
GROUP BY CAST(c.sc AS BINARY), CAST(c.tc AS BINARY);

/* 汇总所有旧 main_py 和附表拼音。
 * source_priority 越小越优先：
 * 1 = 附表中恰好等于所属记录 main_py，可提供最可信标签；
 * 2 = 其他附表读音；
 * 3 = 仅存在于主表的 main_py。
 */
CREATE TEMPORARY TABLE tmp_lac_pinyin_source
(
    sc              VARCHAR(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
    tc              VARCHAR(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
    pinyin          VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
    code_value      VARCHAR(10) NULL,
    tag_sc          VARCHAR(20) NULL,
    tag_tc          VARCHAR(20) NULL,
    old_sort        INT NULL,
    source_priority INT NOT NULL,
    source_id       INT NOT NULL
);

/* 旧主表 main_py 全部进入候选集。 */
INSERT INTO tmp_lac_pinyin_source
    (sc, tc, pinyin, code_value, tag_sc, tag_tc, old_sort, source_priority, source_id)
SELECT
    c.sc,
    c.tc,
    c.main_py,
    c.py_code,
    NULL,
    NULL,
    NULL,
    3,
    c.id
FROM lac_char c;

/* 旧附表读音全部进入候选集；code 从同汉字、同二进制 main_py 的旧主记录取得。 */
INSERT INTO tmp_lac_pinyin_source
    (sc, tc, pinyin, code_value, tag_sc, tag_tc, old_sort, source_priority, source_id)
SELECT
    owner.sc,
    owner.tc,
    cp.pinyin,
    code_source.py_code,
    cp.sc,
    cp.tc,
    cp.sort,
    CASE
        WHEN owner.main_py COLLATE utf8mb4_bin = cp.pinyin COLLATE utf8mb4_bin THEN 1
        ELSE 2
    END,
    cp.id
FROM lac_char_pinyin cp
JOIN lac_char owner ON owner.id = cp.char_id
LEFT JOIN
(
    /* 防止异常旧数据产生多重连接：每个汉字、每个二进制 main_py 只取一条 code。 */
    SELECT sc, tc, main_py, py_code
    FROM
    (
        SELECT
            c.*,
            ROW_NUMBER() OVER (
                PARTITION BY CAST(c.sc AS BINARY), CAST(c.tc AS BINARY), CAST(c.main_py AS BINARY)
                ORDER BY c.id
            ) AS same_pinyin_row_number
        FROM lac_char c
    ) ranked_code
    WHERE same_pinyin_row_number = 1
) code_source
       ON CAST(code_source.sc AS BINARY) = CAST(owner.sc AS BINARY)
      AND CAST(code_source.tc AS BINARY) = CAST(owner.tc AS BINARY)
      AND code_source.main_py COLLATE utf8mb4_bin = cp.pinyin COLLATE utf8mb4_bin;

/* 每个汉字、每个二进制拼音保留一项，并确定标签和排序依据。 */
CREATE TEMPORARY TABLE tmp_lac_pinyin AS
SELECT
    sc,
    tc,
    pinyin,
    code_value,
    tag_sc,
    tag_tc,
    old_sort,
    source_id
FROM
(
    SELECT
        source.*,
        ROW_NUMBER() OVER (
            PARTITION BY CAST(source.sc AS BINARY), CAST(source.tc AS BINARY), CAST(source.pinyin AS BINARY)
            ORDER BY
                source.source_priority,
                CASE WHEN source.old_sort IS NULL THEN 1 ELSE 0 END,
                source.old_sort,
                source.source_id
        ) AS same_pinyin_row_number
    FROM tmp_lac_pinyin_source source
) ranked_pinyin
WHERE same_pinyin_row_number = 1;

/* 每个汉字的方言拼音数量和普通话候选数量。
 * sc/tc 相同不会造成重复，因为按 mdr_char.id 计数。
 */
CREATE TEMPORARY TABLE tmp_lac_mandarin_candidate_count AS
SELECT
    base.sc,
    base.tc,
    (
        SELECT COUNT(*)
        FROM tmp_lac_pinyin dialect_pinyin
        WHERE CAST(dialect_pinyin.sc AS BINARY) = CAST(base.sc AS BINARY)
          AND CAST(dialect_pinyin.tc AS BINARY) = CAST(base.tc AS BINARY)
    ) AS dialect_pinyin_count,
    (
        SELECT COUNT(DISTINCT m.id)
        FROM mdr_char m
        WHERE CAST(m.hanzi AS BINARY) = CAST(base.sc AS BINARY)
           OR CAST(m.hanzi AS BINARY) = CAST(base.tc AS BINARY)
    ) AS mandarin_candidate_count
FROM tmp_lac_hanzi_base base;

/* 将旧映射精确挂到旧 dialect_id 所在记录的 main_py。
 * 只有方言拼音和普通话候选都恰好一个时，才可依靠默认规则省略显式映射。
 * 例如“什”有两个方言拼音、一个普通话候选，仍必须保留普通话属于 sẹt6 的信息。
 */
CREATE TEMPORARY TABLE tmp_lac_explicit_mandarin AS
SELECT DISTINCT
    old_hanzi.sc,
    old_hanzi.tc,
    old_hanzi.main_py AS pinyin,
    old_map.mandarin_id
FROM lac_char_mdr old_map
JOIN lac_char old_hanzi ON old_hanzi.id = old_map.dialect_id
JOIN tmp_lac_mandarin_candidate_count candidate
  ON CAST(candidate.sc AS BINARY) = CAST(old_hanzi.sc AS BINARY)
 AND CAST(candidate.tc AS BINARY) = CAST(old_hanzi.tc AS BINARY)
WHERE candidate.dialect_pinyin_count <> 1
   OR candidate.mandarin_candidate_count <> 1;


/* ========================================================================== */
/* 3. 导入新主表                                                              */
/* ========================================================================== */

INSERT INTO lac_hanzi
    (sc, tc, pinyin, special, note, created_at, updated_at, status)
SELECT
    base.sc,
    base.tc,
    (
        SELECT CAST(
            CONCAT(
                '[',
                GROUP_CONCAT(
                    JSON_OBJECT(
                        'pinyin', py.pinyin,
                        'code', py.code_value,
                        'tag', JSON_OBJECT(
                            'sc', COALESCE(py.tag_sc, '待定'),
                            'tc', COALESCE(py.tag_tc, '待定')
                        ),
                        'mandarin', COALESCE(
                            (
                                SELECT JSON_ARRAYAGG(mapping.mandarin_id)
                                FROM tmp_lac_explicit_mandarin mapping
                                WHERE CAST(mapping.sc AS BINARY) = CAST(py.sc AS BINARY)
                                  AND CAST(mapping.tc AS BINARY) = CAST(py.tc AS BINARY)
                                  AND mapping.pinyin COLLATE utf8mb4_bin = py.pinyin COLLATE utf8mb4_bin
                            ),
                            JSON_ARRAY()
                        )
                    )
                    ORDER BY
                        CASE WHEN py.old_sort IS NULL THEN 1 ELSE 0 END,
                        py.old_sort,
                        py.code_value,
                        py.source_id
                    SEPARATOR ','
                ),
                ']'
            ) AS JSON
        )
        FROM tmp_lac_pinyin py
        WHERE CAST(py.sc AS BINARY) = CAST(base.sc AS BINARY)
          AND CAST(py.tc AS BINARY) = CAST(base.tc AS BINARY)
    ) AS pinyin,
    base.special,
    COALESCE(base.note, JSON_ARRAY()),
    base.created_at,
    base.updated_at,
    base.status
FROM tmp_lac_hanzi_base base;


/* ========================================================================== */
/* 4. 导入相似字                                                              */
/* ========================================================================== */

INSERT INTO lac_hanzi_similar (hanzi_id, sc, tc)
SELECT DISTINCT
    new_hanzi.id,
    old_similar.sc,
    old_similar.tc
FROM lac_char_similar old_similar
JOIN lac_char old_hanzi ON old_hanzi.id = old_similar.char_id
JOIN lac_hanzi new_hanzi
  ON CAST(new_hanzi.sc AS BINARY) = CAST(old_hanzi.sc AS BINARY)
 AND CAST(new_hanzi.tc AS BINARY) = CAST(old_hanzi.tc AS BINARY);


/* ========================================================================== */
/* 5. 导入后校验                                                              */
/* ========================================================================== */

/* 5.1 新表汉字数应等于旧表不同 (sc, tc) 数。 */
SELECT
    (SELECT COUNT(*) FROM lac_hanzi) AS new_hanzi_count,
    (
        SELECT COUNT(*)
        FROM
        (
            SELECT CAST(sc AS BINARY), CAST(tc AS BINARY)
            FROM lac_char
            GROUP BY CAST(sc AS BINARY), CAST(tc AS BINARY)
        ) old_unique
    ) AS expected_hanzi_count;

/* 5.2 新表拼音项数应等于按二进制去重后的旧拼音数。 */
SELECT
    (
        SELECT COUNT(*)
        FROM lac_hanzi h
        JOIN JSON_TABLE(h.pinyin, '$[*]' COLUMNS (pinyin VARCHAR(20) PATH '$.pinyin')) jt
    ) AS new_pinyin_count,
    (SELECT COUNT(*) FROM tmp_lac_pinyin) AS expected_pinyin_count;

/* 5.2.1 如果总数不一致，先定位数量不一致的汉字。 */
SELECT
    expected.sc,
    expected.tc,
    expected.expected_count,
    JSON_LENGTH(actual.pinyin) AS actual_count
FROM
(
    SELECT ANY_VALUE(sc) AS sc, ANY_VALUE(tc) AS tc, COUNT(*) AS expected_count
    FROM tmp_lac_pinyin
    GROUP BY CAST(sc AS BINARY), CAST(tc AS BINARY)
) expected
JOIN lac_hanzi actual
  ON CAST(actual.sc AS BINARY) = CAST(expected.sc AS BINARY)
 AND CAST(actual.tc AS BINARY) = CAST(expected.tc AS BINARY)
WHERE expected.expected_count <> JSON_LENGTH(actual.pinyin);

/* 5.2.2 精确列出未进入新 JSON 的拼音。 */
SELECT
    expected.sc,
    expected.tc,
    expected.pinyin AS missing_pinyin,
    expected.code_value,
    expected.tag_sc,
    expected.tag_tc,
    expected.old_sort,
    expected.source_id
FROM tmp_lac_pinyin expected
JOIN lac_hanzi actual_hanzi
  ON CAST(actual_hanzi.sc AS BINARY) = CAST(expected.sc AS BINARY)
 AND CAST(actual_hanzi.tc AS BINARY) = CAST(expected.tc AS BINARY)
LEFT JOIN JSON_TABLE(
    actual_hanzi.pinyin,
    '$[*]' COLUMNS (
        pinyin VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin PATH '$.pinyin'
    )
) actual_pinyin
  ON CAST(actual_pinyin.pinyin AS BINARY) = CAST(expected.pinyin AS BINARY)
WHERE actual_pinyin.pinyin IS NULL;

/* 5.2.3 独立校验版：不依赖临时表，关闭连接后也可以直接运行。
 * 同时给出旧表二进制去重后的数量和新 JSON 中的数量。
 */
WITH old_pinyin_raw AS
(
    SELECT
        c.sc,
        c.tc,
        CAST(c.main_py AS CHAR CHARACTER SET utf8mb4) COLLATE utf8mb4_bin AS pinyin
    FROM lac_char c

    UNION ALL

    SELECT
        c.sc,
        c.tc,
        CAST(cp.pinyin AS CHAR CHARACTER SET utf8mb4) COLLATE utf8mb4_bin AS pinyin
    FROM lac_char_pinyin cp
    JOIN lac_char c ON c.id = cp.char_id
),
old_pinyin AS
(
    SELECT
        ANY_VALUE(sc) AS sc,
        ANY_VALUE(tc) AS tc,
        CAST(pinyin AS BINARY) AS pinyin_binary
    FROM old_pinyin_raw
    GROUP BY CAST(sc AS BINARY), CAST(tc AS BINARY), CAST(pinyin AS BINARY)
),
new_pinyin AS
(
    SELECT h.id, h.sc, h.tc, py.pinyin
    FROM lac_hanzi h
    JOIN JSON_TABLE(
        h.pinyin,
        '$[*]' COLUMNS (
            pinyin VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin PATH '$.pinyin'
        )
    ) py
)
SELECT
    (SELECT COUNT(*) FROM new_pinyin) AS new_pinyin_count,
    (SELECT COUNT(*) FROM old_pinyin) AS expected_pinyin_count;

/* 5.2.4 独立校验版：旧表有、lac_hanzi.pinyin 中缺失的拼音。 */
WITH old_pinyin_raw AS
(
    SELECT
        c.sc,
        c.tc,
        CAST(c.main_py AS CHAR CHARACTER SET utf8mb4) COLLATE utf8mb4_bin AS pinyin,
        'main_py' AS source_type,
        c.id AS source_id
    FROM lac_char c

    UNION ALL

    SELECT
        c.sc,
        c.tc,
        CAST(cp.pinyin AS CHAR CHARACTER SET utf8mb4) COLLATE utf8mb4_bin AS pinyin,
        'char_pinyin' AS source_type,
        cp.id AS source_id
    FROM lac_char_pinyin cp
    JOIN lac_char c ON c.id = cp.char_id
),
old_pinyin AS
(
    SELECT
        ANY_VALUE(sc) AS sc,
        ANY_VALUE(tc) AS tc,
        ANY_VALUE(pinyin) AS pinyin,
        CAST(pinyin AS BINARY) AS pinyin_binary,
        GROUP_CONCAT(
            CONCAT(source_type, ':', source_id)
            ORDER BY source_type, source_id
        ) AS old_sources
    FROM old_pinyin_raw
    GROUP BY CAST(sc AS BINARY), CAST(tc AS BINARY), CAST(pinyin AS BINARY)
),
new_pinyin AS
(
    SELECT h.sc, h.tc, py.pinyin
    FROM lac_hanzi h
    JOIN JSON_TABLE(
        h.pinyin,
        '$[*]' COLUMNS (
            pinyin VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin PATH '$.pinyin'
        )
    ) py
)
SELECT
    old.sc,
    old.tc,
    old.pinyin AS missing_pinyin,
    old.old_sources
FROM old_pinyin old
LEFT JOIN new_pinyin new
  ON CAST(new.sc AS BINARY) = CAST(old.sc AS BINARY)
 AND CAST(new.tc AS BINARY) = CAST(old.tc AS BINARY)
 AND CAST(new.pinyin AS BINARY) = old.pinyin_binary
WHERE new.pinyin IS NULL
ORDER BY old.sc, old.tc, old.pinyin_binary;

/* 5.2.5 独立校验版：新 JSON 中存在、旧表中找不到的额外拼音。 */
WITH old_pinyin_raw AS
(
    SELECT
        c.sc,
        c.tc,
        CAST(c.main_py AS CHAR CHARACTER SET utf8mb4) COLLATE utf8mb4_bin AS pinyin
    FROM lac_char c

    UNION ALL

    SELECT
        c.sc,
        c.tc,
        CAST(cp.pinyin AS CHAR CHARACTER SET utf8mb4) COLLATE utf8mb4_bin AS pinyin
    FROM lac_char_pinyin cp
    JOIN lac_char c ON c.id = cp.char_id
),
old_pinyin AS
(
    SELECT
        ANY_VALUE(sc) AS sc,
        ANY_VALUE(tc) AS tc,
        CAST(pinyin AS BINARY) AS pinyin_binary
    FROM old_pinyin_raw
    GROUP BY CAST(sc AS BINARY), CAST(tc AS BINARY), CAST(pinyin AS BINARY)
),
new_pinyin AS
(
    SELECT h.id, h.sc, h.tc, py.pinyin
    FROM lac_hanzi h
    JOIN JSON_TABLE(
        h.pinyin,
        '$[*]' COLUMNS (
            pinyin VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin PATH '$.pinyin'
        )
    ) py
)
SELECT
    new.id,
    new.sc,
    new.tc,
    new.pinyin AS extra_pinyin
FROM new_pinyin new
LEFT JOIN old_pinyin old
  ON CAST(old.sc AS BINARY) = CAST(new.sc AS BINARY)
 AND CAST(old.tc AS BINARY) = CAST(new.tc AS BINARY)
 AND old.pinyin_binary = CAST(new.pinyin AS BINARY)
WHERE old.pinyin_binary IS NULL
ORDER BY new.id, CAST(new.pinyin AS BINARY);

/* 5.3 不应出现空拼音或空拼音数组。 */
SELECT h.id, h.sc, h.tc, h.pinyin
FROM lac_hanzi h
WHERE JSON_LENGTH(h.pinyin) = 0
   OR EXISTS
      (
          SELECT 1
          FROM JSON_TABLE(
              h.pinyin,
              '$[*]' COLUMNS (pinyin VARCHAR(20) PATH '$.pinyin')
          ) py
          WHERE py.pinyin IS NULL OR py.pinyin = ''
      );

/* 5.4 code 为 null 的读音需要补算后才能切换应用。 */
SELECT h.id, h.sc, h.tc, py.pinyin
FROM lac_hanzi h
JOIN JSON_TABLE(
    h.pinyin,
    '$[*]' COLUMNS (
        pinyin VARCHAR(20) PATH '$.pinyin',
        code_value VARCHAR(20) PATH '$.code' NULL ON EMPTY
    )
) py
WHERE py.code_value IS NULL;

/* 5.5 同一普通话 ID 不应显式挂在多个方言拼音上。 */
SELECT
    mapping.mandarin_id,
    COUNT(*) AS mapped_pinyin_count,
    GROUP_CONCAT(CONCAT(h.sc, '/', h.tc, ':', py.pinyin) ORDER BY h.id, py.pinyin) AS locations
FROM lac_hanzi h
JOIN JSON_TABLE(
    h.pinyin,
    '$[*]' COLUMNS (
        pinyin VARCHAR(20) PATH '$.pinyin',
        mandarin JSON PATH '$.mandarin'
    )
) py
JOIN JSON_TABLE(
    py.mandarin,
    '$[*]' COLUMNS (mandarin_id INT PATH '$')
) mapping
GROUP BY mapping.mandarin_id
HAVING mapped_pinyin_count > 1;

/* 5.6 JSON 中保存的普通话 ID 必须在 mdr_char 中存在。 */
SELECT h.id, h.sc, h.tc, py.pinyin, mapping.mandarin_id
FROM lac_hanzi h
JOIN JSON_TABLE(
    h.pinyin,
    '$[*]' COLUMNS (
        pinyin VARCHAR(20) PATH '$.pinyin',
        mandarin JSON PATH '$.mandarin'
    )
) py
JOIN JSON_TABLE(
    py.mandarin,
    '$[*]' COLUMNS (mandarin_id INT PATH '$')
) mapping
LEFT JOIN mdr_char m ON m.id = mapping.mandarin_id
WHERE m.id IS NULL;

/* 5.7 示例：二进制精确查询拼音。把下面参数替换成实际值即可。

SELECT DISTINCT h.*
FROM lac_hanzi h
JOIN JSON_TABLE(
    h.pinyin,
    '$[*]' COLUMNS (
        pinyin VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin PATH '$.pinyin'
    )
) py
WHERE py.pinyin COLLATE utf8mb4_bin = 'cẹn' COLLATE utf8mb4_bin;

 */

/* 本脚本到此结束。确认校验结果前，不要删除旧表。 */
