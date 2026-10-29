-- 请由数据库管理员手动执行；应用不会自动建表或修改旧拼音表。
-- 旧的 *_ipa_segment 与 pinyin-table-display-json 暂留供迁移核对。
CREATE TABLE IF NOT EXISTS NC.pinyin_document (
    dialect VARCHAR(16) NOT NULL COMMENT '方言代码',
    payload JSON NOT NULL COMMENT '拼音表 v2：分区、格子、子项与解释',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (dialect)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin
  COMMENT '图形化拼音表文档';

-- 执行后可核对表已建立；首次在编辑器保存前，此查询应返回零行。
SELECT dialect, updated_at FROM NC.pinyin_document;

-- 在编辑器完成导入并点击“保存”后，检查每个方言的新文档及未归类数量。
SELECT dialect,
       JSON_LENGTH(JSON_EXTRACT(payload, '$.sections')) AS section_count,
       JSON_LENGTH(JSON_EXTRACT(payload, '$.unplaced')) AS unplaced_count,
       JSON_LENGTH(JSON_EXTRACT(payload, '$.migratedLegacyKeys')) AS imported_legacy_count,
       updated_at
FROM NC.pinyin_document
ORDER BY dialect;

-- 核对旧记录数量；把 NC.nc_ipa_segment 中的 nc 改成要检查的方言代码。
SELECT COUNT(*) AS legacy_child_count
FROM NC.nc_ipa_segment
WHERE self_key IS NOT NULL;

-- 应返回零行；若有结果，说明该旧子项尚未经过新编辑器的导入核对。
SELECT old.self_key, old.root_key, old.title
FROM NC.nc_ipa_segment AS old
LEFT JOIN NC.pinyin_document AS doc ON doc.dialect = 'nc'
WHERE old.self_key IS NOT NULL
  AND COALESCE(JSON_CONTAINS(JSON_EXTRACT(doc.payload, '$.migratedLegacyKeys'), JSON_QUOTE(old.self_key)), 0) = 0;
