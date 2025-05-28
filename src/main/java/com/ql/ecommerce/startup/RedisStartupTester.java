package com.ql.ecommerce.startup;

import com.ql.ecommerce.entity.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisStartupTester implements CommandLineRunner {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    Logger logger= LoggerFactory.getLogger(RedisStartupTester.class);

    @Override
    public void run(String... args) {
        try {
            redisTemplate.opsForValue().set("startup_test", new Object());
            Object object = redisTemplate.opsForValue().get("startup_test");
            logger.info("Redis connected. Value: {}" , object);
        } catch (Exception e) {
            logger.info("Redis connection failed: {}" , e.getMessage());
        }
    }

}
