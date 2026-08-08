package com.shuowen.yuzong.media.oss;

import com.aliyun.oss.OSS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping ("/oss")
public class OssTestController {


    @Autowired
    private OSS ossClient;


    @Value ("${aliyun.oss.bucketName}")
    private String bucketName;


    @GetMapping ("/test")
    public String test(){

        boolean exists =
                ossClient.doesBucketExist(bucketName);

        return exists ?
                "OSS连接成功"
                :
                "Bucket不存在";
    }
}