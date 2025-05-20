package com.shuowen.yuzong.controller;

import com.shuowen.yuzong.service.impl.RMQSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestRocketmqController {
    @Autowired
    private RMQSender rmqSender;

//    @RequestMapping("/api/message/sync/send")
//    public String sendSyncMessage(){
//        rmqSender.sendSyncMessage("test sendSyncMessage..");
//        return "ok";
//    }
//
//    @RequestMapping("/api/message/async/send")
//    public String sendAsyncMessage(){
//        rmqSender.sendAsyncMessage("test sendAsyncMessage...");
//        return "ok";
//    }

    @RequestMapping("/api/rmq/transaction/message/send")
    public Object sendTransactionMsg(@RequestParam String message){
        try {
            rmqSender.sendTransactionMessage(message);
        }catch (Exception e) {
            System.out.println(e);
            return e.toString();
        }
        return "success";
    }


}
