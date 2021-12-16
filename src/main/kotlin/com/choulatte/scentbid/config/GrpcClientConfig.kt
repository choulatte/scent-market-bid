package com.choulatte.scentbid.config

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GrpcClientConfig {
    @Bean(name = ["pay"])
    fun setPayChannel(): ManagedChannel {
        return ManagedChannelBuilder.forAddress("172.20.10.3", 8090).usePlaintext().build()
    }

    @Bean(name = ["product"])
    fun setProductChannel(): ManagedChannel {
        return ManagedChannelBuilder.forAddress("172.20.10.3", 8090).usePlaintext().build()
    }
}