package com.asap.bootstrap.common.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.servers.Server
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
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

    @Bean
    fun openApi(): OpenAPI {
        return OpenAPI().addSecurityItem(
            SecurityRequirement().addList("bearer-jwt")
        ).components(
            Components().addSecuritySchemes(
                "bearer-jwt",
                SecurityScheme().type(SecurityScheme.Type.HTTP)
                    .bearerFormat("JWT")
                    .scheme("bearer")
            )
        )
    }
}