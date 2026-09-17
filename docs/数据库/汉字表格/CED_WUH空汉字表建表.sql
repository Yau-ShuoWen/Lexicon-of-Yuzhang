/*
 * CED、WUH 空汉字表建表脚本
 *
 * 两个方言目前没有需要迁移的汉字数据，因此只创建与 lac_hanzi 相同的新结构。
 * 本脚本不删除、不修改旧表。
 */

USE NC;

CREATE TABLE ced_hanzi
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    sc         VARCHAR(10) COLLATE utf8mb4_bin     NOT NULL,
    tc         VARCHAR(10) COLLATE utf8mb4_bin     NOT NULL,
    pinyin     JSON                                NOT NULL,
    special    INT                                 NOT NULL COMMENT '0普通；1特殊方言字；2占位字；3不使用；4迁移冲突待人工处理',
    note       JSON                                NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
    status     INT                                 NOT NULL COMMENT '词条状态，含义沿用现有代码',

    CONSTRAINT uk_ced_hanzi_sc_tc UNIQUE (sc, tc),
    CONSTRAINT ck_ced_hanzi_pinyin_array CHECK (JSON_TYPE(pinyin) = 'ARRAY'),
    CONSTRAINT ck_ced_hanzi_note_array CHECK (JSON_TYPE(note) = 'ARRAY'),
    CONSTRAINT ck_ced_hanzi_special CHECK (special BETWEEN 0 AND 4)
)
COLLATE = utf8mb4_general_ci;

CREATE TABLE ced_hanzi_similar
(
    id       INT AUTO_INCREMENT PRIMARY KEY,
    hanzi_id INT                                    NOT NULL,
    sc       VARCHAR(20) COLLATE utf8mb4_bin        NOT NULL,
    tc       VARCHAR(20) COLLATE utf8mb4_bin        NOT NULL,

    CONSTRAINT uk_ced_hanzi_similar UNIQUE (hanzi_id, sc, tc),
    CONSTRAINT fk_ced_hanzi_similar_hanzi
        FOREIGN KEY (hanzi_id) REFERENCES ced_hanzi (id)
)
COLLATE = utf8mb4_general_ci;

CREATE TABLE wuh_hanzi
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    sc         VARCHAR(10) COLLATE utf8mb4_bin     NOT NULL,
    tc         VARCHAR(10) COLLATE utf8mb4_bin     NOT NULL,
    pinyin     JSON                                NOT NULL,
    special    INT                                 NOT NULL COMMENT '0普通；1特殊方言字；2占位字；3不使用；4迁移冲突待人工处理',
    note       JSON                                NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
    status     INT                                 NOT NULL COMMENT '词条状态，含义沿用现有代码',

    CONSTRAINT uk_wuh_hanzi_sc_tc UNIQUE (sc, tc),
    CONSTRAINT ck_wuh_hanzi_pinyin_array CHECK (JSON_TYPE(pinyin) = 'ARRAY'),
    CONSTRAINT ck_wuh_hanzi_note_array CHECK (JSON_TYPE(note) = 'ARRAY'),
    CONSTRAINT ck_wuh_hanzi_special CHECK (special BETWEEN 0 AND 4)
)
COLLATE = utf8mb4_general_ci;

CREATE TABLE wuh_hanzi_similar
(
    id       INT AUTO_INCREMENT PRIMARY KEY,
    hanzi_id INT                                    NOT NULL,
    sc       VARCHAR(20) COLLATE utf8mb4_bin        NOT NULL,
    tc       VARCHAR(20) COLLATE utf8mb4_bin        NOT NULL,

    CONSTRAINT uk_wuh_hanzi_similar UNIQUE (hanzi_id, sc, tc),
    CONSTRAINT fk_wuh_hanzi_similar_hanzi
        FOREIGN KEY (hanzi_id) REFERENCES wuh_hanzi (id)
)
COLLATE = utf8mb4_general_ci;

/* 建表后检查：四个结果都应为 0。 */
SELECT COUNT(*) AS ced_hanzi_count FROM ced_hanzi;
SELECT COUNT(*) AS ced_hanzi_similar_count FROM ced_hanzi_similar;
SELECT COUNT(*) AS wuh_hanzi_count FROM wuh_hanzi;
SELECT COUNT(*) AS wuh_hanzi_similar_count FROM wuh_hanzi_similar;
