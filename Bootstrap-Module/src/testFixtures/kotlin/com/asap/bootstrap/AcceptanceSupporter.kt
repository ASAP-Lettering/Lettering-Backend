package com.asap.bootstrap

import com.asap.security.jwt.TestJwtDataGenerator
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc

@ActiveProfiles("test")
abstract class AcceptanceSupporter {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var testJwtDataGenerator: TestJwtDataGenerator

    @Autowired
    lateinit var objectMapper: ObjectMapper
}