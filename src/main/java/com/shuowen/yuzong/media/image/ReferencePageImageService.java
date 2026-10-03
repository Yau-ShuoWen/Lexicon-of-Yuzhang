package com.shuowen.yuzong.media.image;

import com.shuowen.yuzong.dict.data.domain.Reference.DictCode;
import com.shuowen.yuzong.dict.data.domain.Reference.Page;
import com.shuowen.yuzong.dict.data.mapper.Reference.RefMapper;
import com.shuowen.yuzong.util.ext.sort.FractionIndex;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ReferencePageImageService
{
    private static final Logger log = LoggerFactory.getLogger(ReferencePageImageService.class);

    private final RefMapper refMapper;
    private final ReferencePageImageMapper bindingMapper;
    private final ImageService imageService;

    public ImageVO get(DictCode dictionary, FractionIndex sort)
    {
        Integer markerId = pageMarkerId(dictionary, sort);
        Long imageId = bindingMapper.findImageId(dictionary.getCode(), markerId);
        return imageId == null ? null : imageService.get(imageId);
    }

    public ImageVO upload(DictCode dictionary, FractionIndex sort, MultipartFile file) throws IOException
    {
        Integer markerId = pageMarkerId(dictionary, sort);
        Long previousId = bindingMapper.findImageId(dictionary.getCode(), markerId);
        ImageVO uploaded = imageService.uploadReference(file, dictionary.getCode(), markerId);
        try
        {
            bindingMapper.bind(dictionary.getCode(), markerId, uploaded.getId());
        }
        catch (RuntimeException exception)
        {
            try
            {
                imageService.delete(uploaded.getId());
            }
            catch (RuntimeException cleanupFailure)
            {
                exception.addSuppressed(cleanupFailure);
            }
            throw exception;
        }
        if (previousId != null && !previousId.equals(uploaded.getId()))
        {
            try
            {
                imageService.delete(previousId);
            }
            catch (RuntimeException exception)
            {
                log.warn("旧参考资料图片清理失败，imageId={}", previousId, exception);
            }
        }
        return uploaded;
    }

    private Integer pageMarkerId(DictCode dictionary, FractionIndex sort)
    {
        var rows = Page.readList(refMapper.getPageBySort(dictionary.getCode(), sort.toString()));
        Integer id = rows.get(0).getId();
        if (id == null)
        {
            throw new IllegalArgumentException("参考资料页面不存在");
        }
        return id;
    }
}
