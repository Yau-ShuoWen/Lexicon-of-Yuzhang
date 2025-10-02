//package com.shuowen.yuzong.redis;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//
//@Configuration
//public class RedisConfig {
//
//    @Bean
//    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){
//        RedisTemplate<String, Object> template = new RedisTemplate<>();
//        template.setConnectionFactory(redisConnectionFactory);
//        //设置redis序列化器
//        StringRedisSerializer keySerializer = new StringRedisSerializer();
//        GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer();
//
//        template.setKeySerializer(keySerializer);
//        template.setValueSerializer(valueSerializer);
//        template.setHashKeySerializer(keySerializer);
//        template.setHashValueSerializer(valueSerializer);
//        return template;
//    }
//
//}