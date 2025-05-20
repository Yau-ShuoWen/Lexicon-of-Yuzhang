package com.shuowen.yuzong.mq;

import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(topic = "demo-topic", consumerGroup = "consumer-group", messageModel = MessageModel.CLUSTERING)
public class RMQTransactionConsumer implements RocketMQListener<String> {
    @Override
    public void onMessage(String s) {
        System.out.printf("收到事务消息: %s\n", s);
    }
}

