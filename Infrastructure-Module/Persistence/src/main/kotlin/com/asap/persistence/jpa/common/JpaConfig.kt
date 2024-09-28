package com.asap.persistence.jpa.common

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = ["com.asap.persistence.jpa"])
@EntityScan(basePackages = ["com.asap.persistence.jpa"])
class JpaConfig
