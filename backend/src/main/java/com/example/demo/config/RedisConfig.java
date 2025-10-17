package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {
    @Bean(destroyMethod = "close")
    public redis.clients.jedis.UnifiedJedis jedis(
            @Value("${spring.data.redis.host}") String host,
            @Value("${spring.data.redis.port}") int port) {

        return new redis.clients.jedis.UnifiedJedis("redis://" + host + ":" + port);
    }

}
