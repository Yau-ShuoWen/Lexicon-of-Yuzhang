package com.shuowen.yuzong.mq;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RMQTransactionProducer {
    @Value("${rocketmq.transaction.producer.group}")
    private String producerGroup;

    @Value("${rocketmq.name-server}")
    private String nameServer;


    @Bean
    public TransactionListener transactionListener() {
        System.out.println("transactionListener Bean 正在初始化");
        return new TransactionListener() {
            @Override
            public LocalTransactionState executeLocalTransaction(Message message, Object o) {
                System.out.println("executeLocalTransaction1111.......");
                return LocalTransactionState.COMMIT_MESSAGE;
            }

            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
                System.out.println("checkLocalTransaction1111...");
                return LocalTransactionState.COMMIT_MESSAGE;
            }
        };
    }

    @Bean
    public RocketMQTemplate rocketMQTemplate() {
        // 创建事务生产者
        TransactionMQProducer transactionMQProducer = new TransactionMQProducer(producerGroup);
        transactionMQProducer.setNamesrvAddr(nameServer);
        transactionMQProducer.setTransactionListener(transactionListener());
        // 将生产者注入到 RocketMQTemplate
        RocketMQTemplate rocketMQTemplate = new RocketMQTemplate();
        rocketMQTemplate.setProducer(transactionMQProducer);
        return rocketMQTemplate;
    }

}
