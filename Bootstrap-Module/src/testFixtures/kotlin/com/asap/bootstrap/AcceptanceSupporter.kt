package com.asap.bootstrap

import com.asap.application.user.UserMockManager
import com.asap.security.jwt.JwtMockManager
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
abstract class AcceptanceSupporter {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var jwtMockManager: JwtMockManager

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var userMockManager: UserMockManager
}
