package com.shuowen.yuzong.media.image;

import com.shuowen.yuzong.media.oss.OSSService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.IIOImage;
import javax.imageio.ImageWriteParam;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
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

    public ImageVO uploadDiary(MultipartFile file, Integer diaryId) throws IOException
    {
        ImageVO image = uploadToPrefix(file, "image/diary/" + diaryId + "/");
        imageMapper.markUnreferenced(image.getId());
        return image;
    }

    public List<ImageVO> listDiaryRecycle()
    {
        return imageMapper.listDiaryRecycle().stream().map(this::toVO).toList();
    }

    public void syncDiaryReferences(Integer diaryId, Set<String> references)
    {
        for (Image image : imageMapper.listDiaryImages(diaryId))
        {
            String filename = image.getObjectName().substring(image.getObjectName().lastIndexOf('/') + 1);
            int dot = filename.lastIndexOf('.');
            String publicId = dot > 0 ? filename.substring(0, dot) : "";
            if (references.contains(String.valueOf(image.getId())) || references.contains(publicId))
                imageMapper.markReferenced(image.getId());
            else
                imageMapper.markUnreferenced(image.getId());
        }
    }

    public ImageVO getDiaryImage(Long imageId, Integer diaryId)
    {
        Image image = imageMapper.findById(imageId);
        if (image == null || image.getStatus() == null || image.getStatus() != 1
                || image.getObjectName() == null
                || !image.getObjectName().startsWith("image/diary/" + diaryId + "/"))
        {
            throw new IllegalArgumentException("图片不存在或不属于当前日记");
        }
        return toVO(image);
    }

    public ImageVO getDiaryImage(String publicId, Integer diaryId)
    {
        if (publicId == null || !publicId.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"))
        {
            throw new IllegalArgumentException("图片标识无效");
        }
        Image image = imageMapper.findDiaryByPublicId(diaryId, publicId);
        if (image == null || image.getStatus() == null || image.getStatus() != 1)
        {
            throw new IllegalArgumentException("图片不存在或不属于当前日记");
        }
        return toVO(image);
    }

    public byte[] readDiaryOriginal(String imageId, Integer diaryId) throws IOException
    {
        ImageVO image = imageId.matches("[1-9][0-9]*")
                ? getDiaryImage(Long.valueOf(imageId), diaryId)
                : getDiaryImage(imageId, diaryId);
        return ossService.read(image.getObjectName());
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
        image.setStatus(1);

        String thumbnailName = null;
        byte[] thumbnail = null;
        if (prefix.startsWith("image/diary/") && !"webp".equals(format))
        {
            var decoded = ImageIO.read(new ByteArrayInputStream(bytes));
            if (decoded != null && Math.max(decoded.getWidth(), decoded.getHeight()) > 2000)
            {
                double ratio = 1600.0 / Math.max(decoded.getWidth(), decoded.getHeight());
                int width = Math.max(1, (int) Math.round(decoded.getWidth() * ratio));
                int height = Math.max(1, (int) Math.round(decoded.getHeight() * ratio));
                BufferedImage scaled = new BufferedImage(width, height,
                        "png".equals(format) ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics = scaled.createGraphics();
                try
                {
                    graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                    graphics.drawImage(decoded, 0, 0, width, height, null);
                }
                finally { graphics.dispose(); }
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                if (writeThumbnail(scaled, format, output))
                {
                    thumbnail = output.toByteArray();
                    thumbnailName = objectName.replace("image/diary/", "image/diary-thumbnail/");
                }
            }
        }
        image.setHasThumbnail(thumbnail != null);

        ossService.upload(new ByteArrayInputStream(bytes), objectName);
        try
        {
            if (thumbnail != null) ossService.upload(new ByteArrayInputStream(thumbnail), thumbnailName);
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
                if (thumbnailName != null) ossService.delete(thumbnailName);
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
        if (image.getObjectName() != null && image.getObjectName().startsWith("image/diary/"))
        {
            String filename = image.getObjectName().substring(image.getObjectName().lastIndexOf('/') + 1);
            int dot = filename.lastIndexOf('.');
            if (dot > 0) vo.setPublicId(filename.substring(0, dot));
        }
        vo.setOriginalName(image.getOriginalName());
        vo.setObjectName(image.getObjectName());
        vo.setFormat(image.getFormat());
        vo.setSize(image.getSize());
        vo.setStatus(image.getStatus());
        vo.setCreateTime(image.getCreateTime());
        vo.setRecycleTime(image.getRecycleTime());
        if (image.getStatus() != null && image.getStatus() == 1)
        {
            vo.setUrl(ossService.getUrl(image.getObjectName()));
            if (Boolean.TRUE.equals(image.getHasThumbnail()))
                vo.setThumbnailUrl(ossService.getUrl(image.getObjectName().replace("image/diary/", "image/diary-thumbnail/")));
        }
        return vo;
    }

    private boolean writeThumbnail(BufferedImage image, String format, ByteArrayOutputStream output) throws IOException
    {
        if ("png".equals(format)) return ImageIO.write(image, format, output);
        var writers = ImageIO.getImageWritersByFormatName("jpeg");
        if (!writers.hasNext()) return false;
        var writer = writers.next();
        try (var stream = ImageIO.createImageOutputStream(output))
        {
            writer.setOutput(stream);
            ImageWriteParam options = writer.getDefaultWriteParam();
            options.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            options.setCompressionQuality(0.92f);
            writer.write(null, new IIOImage(image, null, null), options);
            return true;
        }
        finally { writer.dispose(); }
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
