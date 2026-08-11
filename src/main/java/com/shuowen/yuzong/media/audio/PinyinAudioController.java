package com.shuowen.yuzong.media.audio;

import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.util.tuple.Maybe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 拼音音频接口。
 * <p>
 * 供拼音注釋編輯器（上传/管理）与拼音详情页（查询播放）使用。
 * 音频统一存到 OSS 虚拟路径 audio/{dialect}/pinyin/{code}/ 下，
 * 文件名固定为 pronunciation（不管原始文件叫什么名字），
 * 从而保证 OSS key 可预测、可直接查询到。
 */
@RestController
@RequestMapping ("/api/pinyin/audio")
public class PinyinAudioController
{

    @Autowired
    private AudioService audioService;

    /**
     * 上传拼音音频（测试/管理用）。<br>
     * <br>
     * 请求示例：<br>
     * POST /api/pinyin/audio/upload?dialect=lac&code=initial-b<br>
     * <br>
     * form 字段：<br>
     * 1. dialect: 方言代号，如 lac / ced<br>
     * 2. code: 拼音 key，如 initial-b、last-iung（与编辑器 keyList、详情页 item.key 一致）<br>
     * 3. file: 音频文件（mp3/wav/aac/m4a/ogg/flac，最大 50MB）<br>
     * <br>
     * 说明：不管原始文件名是什么，落盘后统一命名为 pronunciation.{ext}，
     * 即 OSS key = audio/{dialect}/pinyin/{code}/pronunciation.{ext}。<br>
     * <br>
     * 返回：落库后的 Audio（含 id、objectName）。
     */
    @PostMapping ("/upload")
    public Audio upload(
            @RequestParam Dialect dialect,
            @RequestParam String code,
            MultipartFile file
    ) throws Exception
    {
        String folderPath = dialect + "/pinyin/" + code;
        return audioService.upload(file, folderPath, "pronunciation");
    }

    /**
     * 按拼音 key 查询音频列表（管理用）。<br>
     * <br>
     * 请求示例：<br>
     * GET /api/pinyin/audio/list?dialect=lac&code=initial-b<br>
     * <br>
     * 参数说明：<br>
     * 1. dialect: 方言代号，如 lac / ced<br>
     * 2. code: 拼音 key<br>
     * <br>
     * 返回：List&lt;AudioVO&gt;，每项含 id/name/format/size/folderPath/url（签名链接）。
     */
    @GetMapping ("/list")
    public List<AudioVO> list(
            @RequestParam Dialect dialect,
            @RequestParam String code)
    {
        String folderPath = dialect + "/pinyin/" + code;
        return audioService.list(folderPath);
    }

    /**
     * 批量查询整个方言的拼音音频（详情页播放用，优化前是逐个 check 请求，这里一次返回全部）。<br>
     * <br>
     * 请求示例：<br>
     * GET /api/pinyin/audio/batch?dialect=lac<br>
     * <br>
     * 参数说明：<br>
     * 1. dialect: 方言代号，如 lac / ced<br>
     * <br>
     * 返回：Map&lt;String, String&gt;，key 为拼音 code（如 initial-b、last-ü），
     * value 为签名 url（可直接播放）。没有音频的 code 不在 map 中。
     * 一次数据库查询 + 批量生成签名 URL，避免前端逐个请求。
     */
    @GetMapping ("/batch")
    public Map<String, String> batch(@RequestParam Dialect dialect)
    {
        // folderPath = "lac/pinyin"，prefix = "audio/lac/pinyin/"
        String folderPath = dialect + "/pinyin";

        Map<String, String> urlMap = new HashMap<>();

        for (AudioVO vo : audioService.list(folderPath))
        {
            // folderPath 形如 "lac/pinyin/initial-b"，截取后半段即为 code
            String code = vo.getFolderPath().substring(folderPath.length() + 1);
            // list 按 create_time DESC，第一个最新；putIfAbsent 保留最新
            urlMap.putIfAbsent(code, vo.getUrl());
        }

        return urlMap;
    }

    /**
     * 查询拼音是否有音频（详情页播放用）。<br>
     * <br>
     * 请求示例：<br>
     * GET /api/pinyin/audio/check?dialect=lac&code=initial-b<br>
     * <br>
     * 参数说明：<br>
     * 1. dialect: 方言代号，如 lac / ced<br>
     * 2. code: 拼音 key<br>
     * <br>
     * 返回：Maybe&lt;AudioVO&gt;（JSON 序列化为 {"empty": true} 或 {"empty": false, "value": {...}}）：<br>
     * - 有音频：{@code {"empty": false, "value": {id, name, url, ...}}}，url 为签名链接可直接播放<br>
     * - 无音频：{@code {"empty": true}}，前端据此不显示播放按钮
     */
    @GetMapping ("/check")
    public Maybe<AudioVO> check(
            @RequestParam Dialect dialect,
            @RequestParam String code)
    {
        String folderPath = dialect + "/pinyin/" + code;
        List<AudioVO> list = audioService.list(folderPath);
        return list.isEmpty() ? Maybe.nothing() : Maybe.exist(list.get(0));
    }

    /**
     * 删除拼音音频：删 OSS 对象 + 软删数据库记录。<br>
     * <br>
     * 请求示例：<br>
     * DELETE /api/pinyin/audio/{id}<br>
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
