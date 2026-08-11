package com.shuowen.yuzong.media.oss;

import lombok.Data;

import java.util.Date;

/**
 * OSS 对象摘要（目录浏览用）。
 * <p>
 * 用于前端测试界面的对象列表展示，只暴露必要字段，避免直接序列化阿里云 SDK 的对象。
 */
@Data
public class OssObjectVO
{
    /** OSS key，例如 test/abc.png */
    private String objectName;

    /** 文件大小（字节） */
    private Long size;

    /** 最后修改时间 */
    private Date lastModified;
}
