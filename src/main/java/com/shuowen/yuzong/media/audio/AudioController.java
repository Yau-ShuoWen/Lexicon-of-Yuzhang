package com.shuowen.yuzong.media.audio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 音频模块测试接口。
 * <p>
 * 仅供调试 / 管理使用；业务代码请直接调用 {@link AudioService} 的方法，
 * 例如编辑词条时同时上传录音：audioService.upload(file, "ciyu/123")。
 */
@RestController
@RequestMapping ("/api/audio")
public class AudioController
{

    @Autowired
    private AudioService audioService;

    /**
     * 上传音频（测试用）。<br>
     * <br>
     * 请求示例：<br>
     * POST /api/audio/upload<br>
     * <br>
     * form 字段：<br>
     * 1. folder: 可选，虚拟文件夹路径，如 wuh/char/114514；不传则放 audio/ 根目录<br>
     * 2. file: 音频文件（mp3/wav/aac/m4a/ogg/flac，最大 50MB）<br>
     * <br>
     * 返回：落库后的 Audio（含 id、objectName、url）。<br>
     * 其中 url 是 30 分钟有效期的签名链接，可直接用于播放。
     */
    @PostMapping ("/upload")
    public Audio upload(
            @RequestParam (required = false) String folder,
            MultipartFile file
    ) throws Exception
    {
        return audioService.upload(file, folder);
    }

    /**
     * 按虚拟文件夹查音频列表（测试用）。<br>
     * <br>
     * 请求示例：<br>
     * GET /api/audio/list?folder=wuh/char<br>
     * <br>
     * 参数说明：<br>
     * 1. folder: 可选，虚拟文件夹路径；不传则列出全部音频<br>
     * <br>
     * 返回：List&lt;AudioVO&gt;，每项含 id/name/format/size/folderPath/url。
     */
    @GetMapping ("/list")
    public List<AudioVO> list(@RequestParam (required = false) String folder)
    {
        return audioService.list(folder);
    }

    /**
     * 音频详情（测试用），含签名 URL。<br>
     * <br>
     * 请求示例：<br>
     * GET /api/audio/{id}
     */
    @GetMapping ("/{id}")
    public AudioVO get(@PathVariable Long id)
    {
        return audioService.get(id);
    }

    /**
     * 删除音频（测试用）：删 OSS 对象 + 软删数据库记录。<br>
     * <br>
     * 请求示例：<br>
     * DELETE /api/audio/{id}<br>
     * <br>
     * 返回：success
     */
    @DeleteMapping ("/{id}")
    public String delete(@PathVariable Long id)
    {
        audioService.delete(id);
        return "success";
    }

}
