package com.shuowen.yuzong.multimedia.audio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping ("/api/audio")
public class AudioUploadController
{

    @Autowired
    private AudioService audioService;

    @PostMapping ("/upload")
    public String upload(AudioUploadRequest req) throws Exception
    {
        return audioService.upload(req);
    }


    @PostMapping ("/pinyin/upload")
    public String uploadP4(PinyinUploadRequest req) throws Exception
    {
        System.out.println(">>> uploadPinyin HIT");
        return audioService.uploadPinyin(req);
    }

    @GetMapping ("/pinyin/exists")
    public boolean exists(String dialect, String pinyinKey)
    {
        return audioService.hasPinyinAudio(dialect, pinyinKey);
    }

    @GetMapping("/pinyin/play")
    public ResponseEntity<Resource> play(
            @RequestParam String dialect,
            @RequestParam String key
    ) throws Exception
    {
        Path path = Paths.get(
                "/data/dictionary/audio/pinyin",
                dialect,
                key + ".mp3"
        );

        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("audio/mpeg"))
                .body(resource);
    }

    @DeleteMapping("/pinyin/delete")
    public String deletePinyin(
            @RequestParam String dialect,
            @RequestParam String key
    ) throws Exception {
        return audioService.deletePinyin(dialect, key);
    }

    @PostMapping(
            value = "/pinyin/replace",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public String replacePinyin(@ModelAttribute PinyinUploadRequest req) throws Exception {
        return audioService.replacePinyin(req);
    }
}