package com.asap.bootstrap.common.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.servers.Server
import org.springframework.context.annotation.Configuration

@Configuration
@OpenAPIDefinition(
    servers = [
        Server(
            url = "http://localhost:8080",
            description = "Server"
        ),
        Server(
            url = "https://api.lettering.world",
            description = "Production Server"
        )
    ]
)
class SwaggerConfig {
}