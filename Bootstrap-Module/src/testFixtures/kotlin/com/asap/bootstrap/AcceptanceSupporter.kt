package com.asap.bootstrap

import com.asap.security.jwt.TestJwtDataGenerator
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc

abstract class AcceptanceSupporter {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var testJwtDataGenerator: TestJwtDataGenerator

    val objectMapper: ObjectMapper = ObjectMapper().registerModules(JavaTimeModule())
}