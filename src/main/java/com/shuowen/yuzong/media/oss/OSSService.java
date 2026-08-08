package com.shuowen.yuzong.media.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class OSSService
{
    @Autowired
    private OSS ossClient;

    @Value ("${aliyun.oss.bucketName}")
    private String bucketName;

    @Value ("${aliyun.oss.endpoint}")
    private String endpoint;

    /**
     * 按固定前缀上传（保留给通用兜底接口使用）。
     */
    public String upload(MultipartFile file) throws IOException
    {
        //生成文件名
        String objectName = "upload/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

        //上传OSS
        ossClient.putObject(bucketName, objectName, file.getInputStream());

        //返回文件地址
        return objectName;
    }

    /**
     * 上传任意文件到任意 OSS key（测试接口用）。<br>
     * <br>
     * 与 {@link #upload(InputStream, String)} 的区别：本方法直接接收 MultipartFile，
     * 供通用上传接口暴露给前端；path 为空时自动生成 upload/{uuid}-{原名} 兜底。
     *
     * @param file 任意文件
     * @param path OSS key，例如 test/abc.png；可传 null/空串，此时使用默认前缀
     * @return 最终落盘的 OSS key
     */
    public String upload(MultipartFile file, String path) throws IOException
    {
        String objectName = normalizePath(path);
        if (objectName.isEmpty())
        {
            objectName = "upload/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
        }

        ossClient.putObject(bucketName, objectName, file.getInputStream());
        return objectName;
    }

    /**
     * 按前缀浏览 OSS 对象（目录查询，测试接口用）。<br>
     * <br>
     * 例如 prefix = "audio/wuh/" 只列出该目录下的对象；空串则列出整个 bucket。
     * 仅返回对象摘要（key / 大小 / 修改时间），不含内容。
     *
     * @param prefix OSS key 前缀
     * @return 对象摘要列表
     */
    public List<OssObjectVO> listObjects(String prefix)
    {
        ObjectListing listing = ossClient.listObjects(bucketName, normalizePath(prefix));

        return listing.getObjectSummaries().stream()
                // 过滤掉 OSS 自动生成的"文件夹"占位对象（以 / 结尾的 key）
                .filter(summary -> !summary.getKey().endsWith("/"))
                .map(this::toVO)
                .toList();
    }

    private OssObjectVO toVO(OSSObjectSummary summary)
    {
        OssObjectVO vo = new OssObjectVO();
        vo.setObjectName(summary.getKey());
        vo.setSize(summary.getSize());
        vo.setLastModified(summary.getLastModified());
        return vo;
    }

    /** 去掉 key 首尾多余的斜杠 */
    private String normalizePath(String path)
    {
        return path == null ? "" : path.trim().replaceAll("^/+|/+$", "");
    }

    /**
     * 直接按指定 key 上传，供音频/图片等业务模块使用。
     *
     * @param in         文件输入流
     * @param objectName OSS key，例如 audio/wuh/char/xxx.mp3
     * @return objectName
     */
    public String upload(InputStream in, String objectName)
    {
        ossClient.putObject(bucketName, objectName, in);
        return objectName;
    }

    /**
     * 默认 30 分钟有效期的签名 URL。
     */
    public String getUrl(String objectName)
    {
        return getUrl(objectName, 30);
    }

    /**
     * 指定有效期（分钟）的签名 URL。
     */
    public String getUrl(String objectName, int expireMinutes)
    {
        Date expiration = new Date(System.currentTimeMillis() + 1000L * 60 * expireMinutes);
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, objectName);
        request.setExpiration(expiration);
        URL url = ossClient.generatePresignedUrl(request);
        return url.toString();
    }

    /**
     * 删除 OSS 对象。
     */
    public void delete(String objectName)
    {
        ossClient.deleteObject(bucketName, objectName);
    }

}
