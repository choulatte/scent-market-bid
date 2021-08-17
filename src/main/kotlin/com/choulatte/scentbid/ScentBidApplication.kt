package com.choulatte.scentbid

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories

@EnableRedisRepositories
@SpringBootApplication
class ScentBidApplication

fun main(args: Array<String>) {
    runApplication<ScentBidApplication>(*args)
}
