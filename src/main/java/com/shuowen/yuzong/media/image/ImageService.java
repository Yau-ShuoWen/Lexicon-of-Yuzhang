package com.shuowen.yuzong.media.image;

import com.shuowen.yuzong.media.oss.OSSService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService
{
    private static final long MAX_SIZE = 10L * 1024 * 1024;
    private static final String PREFIX = "image/manual/";

    private final OSSService ossService;
    private final ImageMapper imageMapper;

    public ImageVO upload(MultipartFile file) throws IOException
    {
        return uploadToPrefix(file, PREFIX);
    }

    public ImageVO uploadReference(MultipartFile file, String dictionary, Integer pageMarkerId) throws IOException
    {
        if (dictionary == null || !dictionary.matches("[A-Za-z0-9_-]{1,32}") || pageMarkerId == null || pageMarkerId <= 0)
        {
            throw new IllegalArgumentException("参考资料页面标识无效");
        }
        return uploadToPrefix(file, "image/reference/" + dictionary.toLowerCase(java.util.Locale.ROOT)
                + "/" + pageMarkerId + "/");
    }

    private ImageVO uploadToPrefix(MultipartFile file, String prefix) throws IOException
    {
        if (file == null || file.isEmpty())
        {
            throw new IllegalArgumentException("请选择图片");
        }
        if (file.getSize() > MAX_SIZE)
        {
            throw new IllegalArgumentException("图片不能超过 10 MB");
        }
        byte[] bytes = file.getBytes();
        String format = detectFormat(bytes);
        if (format == null)
        {
            throw new IllegalArgumentException("只支持 JPEG、PNG、WebP 图片");
        }
        if (!"webp".equals(format))
        {
            var decoded = ImageIO.read(new ByteArrayInputStream(bytes));
            if (decoded == null || (long) decoded.getWidth() * decoded.getHeight() > 40_000_000L)
            {
                throw new IllegalArgumentException("图片内容无效或尺寸过大");
            }
        }

        String objectName = prefix + UUID.randomUUID() + "." + format;
        Image image = new Image();
        image.setOriginalName(safeName(file.getOriginalFilename()));
        image.setObjectName(objectName);
        image.setFormat(format);
        image.setSize(file.getSize());

        ossService.upload(new ByteArrayInputStream(bytes), objectName);
        try
        {
            if (imageMapper.insert(image) != 1)
            {
                throw new IllegalStateException("图片记录保存失败");
            }
        }
        catch (RuntimeException exception)
        {
            try
            {
                ossService.delete(objectName);
            }
            catch (RuntimeException cleanupFailure)
            {
                exception.addSuppressed(cleanupFailure);
            }
            throw exception;
        }
        return toVO(image);
    }

    public List<ImageVO> list()
    {
        return imageMapper.listActive().stream().map(this::toVO).toList();
    }

    public ImageVO get(Long id)
    {
        Image image = imageMapper.findById(id);
        if (image == null || image.getStatus() == null || image.getStatus() != 1)
        {
            throw new IllegalArgumentException("图片不存在");
        }
        return toVO(image);
    }

    public void delete(Long id)
    {
        Image image = imageMapper.findById(id);
        if (image == null || image.getStatus() == null || image.getStatus() == 0)
        {
            return;
        }
        if (image.getStatus() == 1 && imageMapper.markDeleting(id) != 1)
        {
            throw new IllegalStateException("图片状态已变化，请重试");
        }
        ossService.delete(image.getObjectName());
        if (imageMapper.finishDelete(id) != 1)
        {
            throw new IllegalStateException("图片删除状态保存失败，请重试");
        }
    }

    private ImageVO toVO(Image image)
    {
        ImageVO vo = new ImageVO();
        vo.setId(image.getId());
        vo.setOriginalName(image.getOriginalName());
        vo.setObjectName(image.getObjectName());
        vo.setFormat(image.getFormat());
        vo.setSize(image.getSize());
        vo.setStatus(image.getStatus());
        vo.setCreateTime(image.getCreateTime());
        if (image.getStatus() != null && image.getStatus() == 1)
        {
            vo.setUrl(ossService.getUrl(image.getObjectName()));
        }
        return vo;
    }

    private String safeName(String name)
    {
        String value = name == null ? "未命名图片" : name.replaceAll("[\\p{Cc}/\\\\]", "").trim();
        return value.isEmpty() ? "未命名图片" : value.substring(0, Math.min(value.length(), 255));
    }

    private String detectFormat(byte[] bytes)
    {
        if (bytes.length >= 3 && (bytes[0] & 0xff) == 0xff
                && (bytes[1] & 0xff) == 0xd8 && (bytes[2] & 0xff) == 0xff)
        {
            return "jpg";
        }
        if (bytes.length >= 8 && (bytes[0] & 0xff) == 0x89
                && bytes[1] == 'P' && bytes[2] == 'N' && bytes[3] == 'G'
                && bytes[4] == 13 && bytes[5] == 10
                && (bytes[6] & 0xff) == 26 && bytes[7] == 10)
        {
            return "png";
        }
        if (bytes.length >= 12 && bytes[0] == 'R' && bytes[1] == 'I'
                && bytes[2] == 'F' && bytes[3] == 'F'
                && bytes[8] == 'W' && bytes[9] == 'E'
                && bytes[10] == 'B' && bytes[11] == 'P')
        {
            return "webp";
        }
        return null;
    }
}
