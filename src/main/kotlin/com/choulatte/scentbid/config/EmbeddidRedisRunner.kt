package com.choulatte.scentbid.config

import org.springframework.beans.factory.DisposableBean
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import redis.embedded.RedisServer

@Component
class EmbeddedRedisRunner(
    val redisServer: RedisServer?
) : ApplicationRunner, DisposableBean {
    override fun run(args: ApplicationArguments) {
        redisServer?.start()
    }

    override fun destroy() {
        redisServer?.stop()
    }
}