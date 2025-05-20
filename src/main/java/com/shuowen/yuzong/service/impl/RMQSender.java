package com.shuowen.yuzong.service.impl;

import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RMQSender {
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Value("${rocketmq.transaction.topic}")
    private String transactionTopic;

//    @Value("${rocketmq.topic}")
//    private String topic;
//
//
//    // 1.同步发送消息
//    public void sendSyncMessage(String message){
//        rocketMQTemplate.syncSend(topic, MessageBuilder.withPayload(message).build());
//        System.out.printf("同步发送结果: %s\n", message);
//    }
//
//    // 2.异步发送消息
//    public void sendAsyncMessage(String message){
//        rocketMQTemplate.asyncSend(topic, MessageBuilder.withPayload(message).build(), new SendCallback() {
//            @Override
//            public void onSuccess(SendResult sendResult) {
//                System.out.printf("异步发送成功: %s\n", sendResult);
//            }
//
//            @Override
//            public void onException(Throwable throwable) {
//                System.out.printf("异步发送失败: %s\n", throwable.getMessage());
//            }
//        });
//    }


    // 3.发送事务消息
    public void sendTransactionMessage(String message){
        // 构建消息
        Message<String> transactionMessage = MessageBuilder.withPayload(message)
                .setHeader(RocketMQHeaders.KEYS, UUID.randomUUID())
                .build();

        // 发送事务消息
        TransactionSendResult result = rocketMQTemplate.sendMessageInTransaction(transactionTopic, transactionMessage, null);

        if (result.getSendStatus() == SendStatus.SEND_OK) {
            System.out.printf("事务消息发送成功! 消息ID: %s\n", result.getMsgId());
        } else {
            System.err.printf("事务消息发送失败! 发送状态: %s\n", result.getSendStatus());
        }
    }
}
