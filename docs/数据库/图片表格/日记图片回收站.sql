-- 请由项目负责人手动执行；只增加标记列，不删除或移动任何原图。
ALTER TABLE image ADD COLUMN recycle_time DATETIME NULL DEFAULT NULL;
ALTER TABLE image ADD COLUMN has_thumbnail BOOLEAN NOT NULL DEFAULT FALSE;
CREATE INDEX idx_image_recycle_time ON image (recycle_time);
