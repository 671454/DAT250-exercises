package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {
    @Bean(destroyMethod = "close")
    public redis.clients.jedis.UnifiedJedis jedis() {
        return new redis.clients.jedis.UnifiedJedis("redis://127.0.0.1:6379");
    }

}
