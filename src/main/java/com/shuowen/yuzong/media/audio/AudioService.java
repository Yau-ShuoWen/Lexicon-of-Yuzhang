package com.shuowen.yuzong.media.audio;

import com.shuowen.yuzong.media.oss.OSSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class AudioService
{
    /** 允许上传的音频格式白名单 */
    private static final Set<String> ALLOWED_FORMATS = Set.of("mp3", "wav", "aac", "m4a", "ogg", "flac");

    @Autowired
    private OSSService ossService;

    @Autowired
    private AudioMapper audioMapper;

    /**
     * 上传音频（供项目各处复用的核心方法）。
     * <p>
     * 任意业务接口在接收"普通字段 + 音频文件"时，直接调用本方法即可，
     * 不需要关心 OSS 细节：文件会存到 audio/{folderPath}/{uuid}.{ext}，
     * 并在 audio 表落一条记录。
     *
     * @param file       音频文件
     * @param folderPath 虚拟文件夹路径（语义化分层），例如 "wuh/char/114514"；
     *                   传 null 或空串则放 audio/ 根目录
     * @return 落库后的 Audio，含 id 与 objectName
     */
    public Audio upload(MultipartFile file, String folderPath) throws Exception
    {
        // 1. 校验文件
        if (file == null || file.isEmpty())
        {
            throw new IllegalArgumentException("音频文件不能为空");
        }
        String originalName = file.getOriginalFilename();
        String ext = getFormat(originalName);
        if (!ALLOWED_FORMATS.contains(ext))
        {
            throw new IllegalArgumentException("不支持的音频格式：" + ext);
        }

        // 2. 构造 OSS key：audio/{folderPath}/{uuid}.{ext}
        String folder = normalizeFolder(folderPath);
        String objectName = "audio/" + (folder.isEmpty() ? "" : folder + "/")
                + UUID.randomUUID() + "." + ext;

        // 3. 上传 OSS 并落库
        ossService.upload(file.getInputStream(), objectName);

        Audio audio = new Audio();
        audio.setName(originalName.substring(0, originalName.lastIndexOf(".")));
        audio.setOriginalName(originalName);
        audio.setObjectName(objectName);
        audio.setFormat(ext);
        audio.setSize(file.getSize());
        audio.setStatus(1);
        audioMapper.insert(audio);

        return audio;
    }

    /**
     * 按虚拟文件夹查询音频列表（folder 为空查全部）。
     */
    public List<AudioVO> list(String folderPath)
    {
        String folder = normalizeFolder(folderPath);
        String prefix = "audio/" + (folder.isEmpty() ? "" : folder + "/");
        return audioMapper.listByFolder(prefix).stream().map(this::toVO).toList();
    }

    /**
     * 查询单个音频详情（含签名 URL）。
     */
    public AudioVO get(Long id)
    {
        Audio audio = audioMapper.findById(id);
        return audio == null ? null : toVO(audio);
    }

    /**
     * 删除音频：删 OSS 对象 + 软删数据库记录。
     */
    public void delete(Long id)
    {
        Audio audio = audioMapper.findById(id);
        if (audio == null)
        {
            return;
        }
        ossService.delete(audio.getObjectName());
        audioMapper.delete(id);
    }

    private AudioVO toVO(Audio audio)
    {
        AudioVO vo = new AudioVO();
        vo.setId(audio.getId());
        vo.setName(audio.getName());
        vo.setFormat(audio.getFormat());
        vo.setSize(audio.getSize());
        vo.setFolderPath(deriveFolder(audio.getObjectName()));
        vo.setUrl(ossService.getUrl(audio.getObjectName()));
        return vo;
    }

    /** 从原始文件名解析扩展名，统一小写；缺少扩展名视为不支持 */
    private String getFormat(String name)
    {
        if (name == null || !name.contains("."))
        {
            throw new IllegalArgumentException("文件名缺少扩展名");
        }
        return name.substring(name.lastIndexOf(".") + 1).toLowerCase(Locale.ROOT);
    }

    /** 规范化文件夹路径：去首尾斜杠、只允许安全字符，防止路径穿越 */
    private String normalizeFolder(String folderPath)
    {
        if (folderPath == null)
        {
            return "";
        }
        String folder = folderPath.trim().replaceAll("^/+|/+$", "");
        if (!folder.matches("[0-9a-zA-Z_/-]*"))
        {
            throw new IllegalArgumentException("文件夹路径包含非法字符：" + folderPath);
        }
        return folder;
    }

    /** 从 objectName 反推虚拟文件夹路径，例如 "audio/wuh/char/xxx.mp3" -> "wuh/char" */
    private String deriveFolder(String objectName)
    {
        String body = objectName.replaceFirst("^audio/", "");
        int slash = body.lastIndexOf("/");
        return slash < 0 ? "" : body.substring(0, slash);
    }
}
