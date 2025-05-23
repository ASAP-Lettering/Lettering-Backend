package com.asap.bootstrap

import com.asap.application.ApplicationConfig
import com.asap.aws.AwsConfig
import com.asap.client.ClientConfig
import com.asap.event.EventConfig
import com.asap.persistence.PersistenceConfig
import com.asap.security.SecurityConfig
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(
    value = [
        ApplicationConfig::class,
        ClientConfig::class,
        SecurityConfig::class,
        AwsConfig::class,
        PersistenceConfig::class,
        EventConfig::class,
    ],
)
class BootstrapApplication

fun main(args: Array<String>) {
    SpringApplication.run(BootstrapApplication::class.java, *args)
}
