package com.example.demo;

import ch.qos.logback.core.net.SyslogOutputStream;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.UnifiedJedis;

import java.util.Map;

//@EnableCaching
@SpringBootApplication
@RestController //Endpoint annotation
public class DemoApplication {

	public static void main(String[] args) {

        SpringApplication.run(DemoApplication.class, args);
        UnifiedJedis jedis = new UnifiedJedis("redis://localhost:6379");

        // Code that interacts with Redis...
        String PONG = jedis.ping();
        System.out.println(PONG);

        final String KEY = "logged_in_users";
        jedis.del(KEY); // Delete everything with this key
        jedis.sadd(KEY, "alice");
        jedis.sadd(KEY, "bob");
        jedis.srem(KEY, "alice");
        jedis.sadd(KEY, "eve");
        System.out.println("All logged in: " + jedis.smembers(KEY));
        System.out.println("Count: " + jedis.scard(KEY));
        System.out.println("Is bob loged in ? " + jedis.sismember(KEY, "bob"));
        jedis.del(KEY); // Delete everything with this key

        final String pollId = "test_pollId";

        jedis.hset("poll:"+pollId, Map.of("title", "Pinnaple on pizza?"));
        jedis.hset("poll:" + pollId + ":captions", Map.of("0", "YES", "1", "NO", "3", "MAYBE"));
        jedis.hset("poll:"+pollId+":counts",
                Map.of("0", "299", "1", "399", "3", "14")
        );

        System.out.println("All counts " + jedis.hgetAll("poll:"+pollId+":counts"));
        jedis.del(pollId); // Delete everything with this key




        jedis.close();
	}


}
