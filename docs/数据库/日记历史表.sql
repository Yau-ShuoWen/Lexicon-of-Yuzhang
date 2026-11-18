-- 请在部署包含日记历史记录功能的后端之前，由数据库管理员手动执行。
-- 从当前日记表复制列和数据类型，历史表保留每次保存前的完整旧记录。
CREATE TABLE NC.ysw_diary_history AS
SELECT * FROM NC.ysw_diary WHERE 1 = 0;

-- 原日记 id 在历史表中允许重复，每一次保存生成独立的 history_id。
ALTER TABLE NC.ysw_diary_history
    ADD COLUMN history_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY FIRST,
    ADD COLUMN archived_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    ADD INDEX idx_ysw_diary_history_diary_id_time (id, archived_at);
