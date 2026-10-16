-- 请由项目负责人手动执行；Codex 不操作数据库。
-- 与当前实现对应。user_id 类型须先核对 NC.user.id。
CREATE TABLE IF NOT EXISTS NC.user_setting (
    user_id INT NOT NULL,
    setting_key VARCHAR(80) NOT NULL,
    setting_value VARCHAR(2048) NOT NULL,
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
        ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (user_id, setting_key),
    CONSTRAINT fk_user_setting_user FOREIGN KEY (user_id) REFERENCES NC.user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS NC.user_setting_version (
    user_id INT NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY (user_id),
    CONSTRAINT fk_user_setting_version_user FOREIGN KEY (user_id) REFERENCES NC.user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
