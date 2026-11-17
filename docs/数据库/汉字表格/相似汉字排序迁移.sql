-- 手动执行。先备份 NC.lac_hanzi_similar；仅在该表尚无 sort_order 列时执行一次。
-- 旧数据没有明确顺序，按同一汉字下的 ID 升序编号。
SELECT id, hanzi_id, sc, tc
FROM NC.lac_hanzi_similar
ORDER BY hanzi_id, id;

ALTER TABLE NC.lac_hanzi_similar
    ADD COLUMN sort_order INT NULL COMMENT '同一汉字内相似汉字的显示顺序';

CREATE TEMPORARY TABLE tmp_hanzi_similar_sort AS
SELECT id, ROW_NUMBER() OVER (PARTITION BY hanzi_id ORDER BY id) AS position
FROM NC.lac_hanzi_similar;

UPDATE NC.lac_hanzi_similar AS similar
JOIN tmp_hanzi_similar_sort AS initial_sort ON initial_sort.id = similar.id
SET similar.sort_order = initial_sort.position;

DROP TEMPORARY TABLE tmp_hanzi_similar_sort;

-- 核对各汉字的顺序与空值。
SELECT id, hanzi_id, sc, tc, sort_order
FROM NC.lac_hanzi_similar
ORDER BY hanzi_id, sort_order, id;
