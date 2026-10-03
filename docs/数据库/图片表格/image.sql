-- 请由项目负责人手动执行；Codex 不操作数据库。
-- 此表用于图片 OSS 全流程验证，status: 1=有效，2=删除中，0=已删除。
CREATE TABLE IF NOT EXISTS image (
    id BIGINT NOT NULL AUTO_INCREMENT,
    original_name VARCHAR(255) NOT NULL,
    object_name VARCHAR(500) NOT NULL,
    format VARCHAR(16) NOT NULL,
    size BIGINT NOT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_image_object_name (object_name),
    KEY idx_image_status_id (status, id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
