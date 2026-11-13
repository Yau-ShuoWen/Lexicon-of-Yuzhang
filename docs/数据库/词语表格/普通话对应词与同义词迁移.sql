-- 手动执行；当前启用方言为 lac。执行前备份 NC.lac_word_similar。
-- 先检查 type=1 的旧数据；旧 type=1 统一按“相等”迁移，需人工核对近似项。
SELECT id, word_id, sc, tc, type
FROM NC.lac_word_similar
WHERE type = 1
ORDER BY word_id, id;

ALTER TABLE NC.lac_word_similar
    ADD COLUMN mandarin_relation VARCHAR(3) NULL COMMENT '普通话对应词：= 或 ≈',
    ADD COLUMN description TEXT NULL COMMENT '对应词或同义词说明，保存简繁 JSON 对象',
    ADD COLUMN sort_order INT NULL COMMENT '同一词条内关联词的显示顺序';

UPDATE NC.lac_word_similar
SET mandarin_relation = '='
WHERE type = 1 AND mandarin_relation IS NULL;

-- 旧表没有顺序字段；按原先通常使用的 id 顺序编号，避免迁移后顺序跳动。
CREATE TEMPORARY TABLE tmp_word_similar_sort AS
SELECT id, ROW_NUMBER() OVER (PARTITION BY word_id ORDER BY id) AS position
FROM NC.lac_word_similar;

UPDATE NC.lac_word_similar AS relation
JOIN tmp_word_similar_sort AS initial_sort ON initial_sort.id = relation.id
SET relation.sort_order = initial_sort.position;

DROP TEMPORARY TABLE tmp_word_similar_sort;

-- 检查迁移结果；将实际为近似关系的记录人工 UPDATE 为 ≈。
SELECT id, word_id, sc, tc, type, mandarin_relation, description, sort_order
FROM NC.lac_word_similar
WHERE type IN (1, 4)
ORDER BY word_id, id;
