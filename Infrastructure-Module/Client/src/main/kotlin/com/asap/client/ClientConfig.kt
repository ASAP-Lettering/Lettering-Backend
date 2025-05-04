package com.asap.client

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(basePackages = ["com.asap.client"])
@EnableConfigurationProperties(ClientProperties::class)
class ClientConfig
