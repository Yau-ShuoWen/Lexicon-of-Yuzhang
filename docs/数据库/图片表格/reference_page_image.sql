-- 请由项目负责人手动执行。运行前应已创建 image 表。
-- page_marker_id 对应 reference 中一页的 /FRONT/ 记录 ID，排序重建后仍保持稳定。
CREATE TABLE IF NOT EXISTS reference_page_image (
    dictionary VARCHAR(32) NOT NULL,
    page_marker_id INT NOT NULL,
    image_id BIGINT NOT NULL,
    PRIMARY KEY (dictionary, page_marker_id),
    KEY idx_reference_page_image_image (image_id),
    CONSTRAINT fk_reference_page_image_image
        FOREIGN KEY (image_id) REFERENCES image(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
