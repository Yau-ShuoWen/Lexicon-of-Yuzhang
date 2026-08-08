package com.shuowen.yuzong.media.oss;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


/**
 * OSS 通用接口（测试/调试用）。
 * <p>
 * 业务代码请直接调用 {@link OSSService}，不要走本接口。
 * 本接口主要给前端测试界面演示"任意路径上传 + 查询"。
 */
@RestController
@RequestMapping ("/upload")
public class UploadController
{

    @Autowired
    private OSSService ossService;


    /**
     * 上传任意文件到任意路径。<br>
     * <br>
     * 请求示例：<br>
     * POST /upload<br>
     * <br>
     * form 字段：<br>
     * 1. path: 可选，OSS key（虚拟路径），如 test/abc.png、audio/wuh/114514/x.mp3；<br>
     *    留空则自动生成 upload/{uuid}-{原文件名}<br>
     * 2. file: 任意文件（最大 50MB）<br>
     * <br>
     * 返回：OSS objectName（String），可直接再用 /upload/url 换取访问链接。
     */
    @PostMapping
    public String upload(
            @RequestParam (required = false) String path,
            MultipartFile file
    ) throws Exception
    {
        return ossService.upload(file, path);
    }

    /**
     * 查询对象访问链接（30 分钟有效期的签名 URL）。<br>
     * <br>
     * 请求示例：<br>
     * GET /upload/url?objectName=test/abc.png<br>
     * <br>
     * 返回：签名 URL（String），图片/音频可直接预览。
     */
    @GetMapping ("/url")
    public String getUrl(@RequestParam String objectName)
    {
        return ossService.getUrl(objectName);
    }

    /**
     * 按前缀浏览 OSS 对象（目录查询）。<br>
     * <br>
     * 请求示例：<br>
     * GET /upload/list?prefix=audio/wuh/<br>
     * <br>
     * 参数说明：<br>
     * 1. prefix: 可选，key 前缀；留空列出整个 bucket 的对象<br>
     * <br>
     * 返回：List&lt;OssObjectVO&gt;，每项含 objectName / size / lastModified。
     */
    @GetMapping ("/list")
    public List<OssObjectVO> list(@RequestParam (required = false) String prefix)
    {
        return ossService.listObjects(prefix);
    }

    /**
     * 删除 OSS 对象。<br>
     * <br>
     * 请求示例：<br>
     * DELETE /upload?objectName=test/abc.png<br>
     * <br>
     * 返回：success
     */
    @DeleteMapping
    public String delete(@RequestParam String objectName)
    {
        ossService.delete(objectName);
        return "success";
    }

}
