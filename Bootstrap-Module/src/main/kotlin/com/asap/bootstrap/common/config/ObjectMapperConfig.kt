package com.asap.bootstrap.common.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.text.SimpleDateFormat

@Configuration
class ObjectMapperConfig {


    @Bean
    fun objectMapper(): ObjectMapper {
        return ObjectMapper()
            .registerModule(JavaTimeModule())
            .setDateFormat(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"))
            .registerKotlinModule()
    }
}