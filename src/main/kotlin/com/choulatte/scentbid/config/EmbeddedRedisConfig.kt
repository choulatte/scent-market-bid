package com.choulatte.scentbid.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import redis.embedded.RedisServer

@Configuration
class EmbeddedRedisConfig(
    @Value("\${spring.redis.port}")
    var redisPort: Int
) {
    @Bean
    fun embeddedRedis(): RedisServer {
        return RedisServer.builder().port(redisPort).build()
    }
}