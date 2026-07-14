package com.shuowen.yuzong.multimedia.audio;

import com.shuowen.yuzong.util.text.ScTcText;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class AudioService
{
    @Autowired
    private AudioResourceMapper mapper;

    private final String BASE_PATH = "/data/dictionary/audio";

    public String upload(AudioUploadRequest req) throws Exception
    {

        String dialect = req.getDialect();
        String type = req.getType();

        ScTcText text = req.getRefKey();
        String sc = text.getSc().toString();
        String tc = text.getTc().toString();

        // 1️⃣ 目录
        String dir = BASE_PATH + "/" + type + "/" + dialect;
        File folder = new File(dir);
        if (!folder.exists()) folder.mkdirs();

        // 2️⃣ 文件名（简单版：UUID）
        String fileName = UUID.randomUUID().toString() + ".mp3";
        File target = new File(folder, fileName);

        // 3️⃣ 保存文件
        req.getFile().transferTo(target);

        // 4️⃣ 写数据库
        AudioResource ar = new AudioResource();
        ar.setDialect(dialect);
        ar.setType(type);
        ar.setSc(sc);
        ar.setTc(tc);
        ar.setFilePath(target.getAbsolutePath());

        mapper.insert(ar);

        return "/audio/" + type + "/" + dialect + "/" + fileName;
    }

    public String uploadPinyin(PinyinUploadRequest req) throws Exception
    {
        String dialect = req.getDialect();
        String pinyinKey = req.getPinyinKey();

        // 1️⃣ 统一服务器存储路径
        Path dir = Paths.get("/data/dictionary/audio/pinyin", dialect);
        Files.createDirectories(dir);

        // 2️⃣ 文件名 = key（关键：不要UUID）
        Path filePath = dir.resolve(pinyinKey + ".mp3");

        // 3️⃣ 写文件（唯一存储来源）
        try (var in = req.getFile().getInputStream())
        {
            Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        // 4️⃣ DB（统一记录）
        AudioResource ar = new AudioResource();
        ar.setDialect(dialect);
        ar.setType("pinyin");
        ar.setSc(pinyinKey);
        ar.setTc(pinyinKey);
        ar.setFilePath(filePath.toString()); // 或者后面可改成相对路径

        mapper.insert(ar);

        // 5️⃣ 返回访问路径（前端用）
        return "/audio/pinyin/" + dialect + "/" + pinyinKey + ".mp3";
    }

    public boolean hasPinyinAudio(String dialect, String pinyinKey)
    {
        return mapper.countPinyinAudio(dialect, pinyinKey) > 0;
    }

    public String deletePinyin(String dialect, String key) throws Exception
    {
        // 1. 查数据库
        AudioResource record = mapper.findByDialectAndScAndType(
                dialect,
                key,
                "pinyin"
        );

        if (record == null)
        {
            return "NOT_FOUND";
        }

        // 2. 删除文件
        Path filePath = Paths.get(record.getFilePath());

        Files.deleteIfExists(filePath);

        // 3. 删除数据库记录
        mapper.deleteById(record.getId());

        return "OK";
    }

    public String replacePinyin(PinyinUploadRequest req) throws Exception
    {
        String dialect = req.getDialect();
        String key = req.getPinyinKey();

        // 1. 先删旧的（文件 + DB）
        AudioResource old = mapper.findByDialectAndScAndType(dialect, key, "pinyin");

        if (old != null)
        {
            Files.deleteIfExists(Paths.get(old.getFilePath()));
            mapper.deleteById(old.getId());
        }

        // 2. 写新文件
        Path dir = Paths.get("/data/dictionary/audio/pinyin", dialect);
        Files.createDirectories(dir);

        Path filePath = dir.resolve(key + ".mp3");

        try (var in = req.getFile().getInputStream())
        {
            Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        // 3. 写DB
        AudioResource ar = new AudioResource();
        ar.setDialect(dialect);
        ar.setType("pinyin");
        ar.setSc(key);
        ar.setTc(key);
        ar.setFilePath(filePath.toString());

        mapper.insert(ar);

        return "/audio/pinyin/" + dialect + "/" + key + ".mp3";
    }
}



