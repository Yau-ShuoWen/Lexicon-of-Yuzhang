-- 请手动执行；按 id 顺序查看接口错误。
CREATE TABLE IF NOT EXISTS NC.api_error_log (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    request_path VARCHAR(500) NOT NULL,
    error_source VARCHAR(40) NOT NULL,
    exception_type VARCHAR(255) NOT NULL,
    error_message VARCHAR(2000) NULL,
    stack_trace TEXT NULL,
    INDEX idx_api_error_log_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 查看最近记录：
-- SELECT id, created_at, request_path, error_source, exception_type, error_message
-- FROM NC.api_error_log ORDER BY id DESC LIMIT 50;
