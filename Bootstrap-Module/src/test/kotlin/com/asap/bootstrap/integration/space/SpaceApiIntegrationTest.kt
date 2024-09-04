package com.asap.bootstrap.integration.space

import com.asap.bootstrap.IntegrationSupporter
import com.asap.security.jwt.TestJwtDataGenerator
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.get

class SpaceApiIntegrationTest: IntegrationSupporter() {

    @Autowired
    lateinit var testJwtDataGenerator: TestJwtDataGenerator

    @Test
    fun getMainSpaceId() {
        // when
        val accessToken = testJwtDataGenerator.generateAccessToken()
        val response = mockMvc.get("/api/v1/spaces/main") {
            header("Authorization", "Bearer $accessToken")
        }

        // then
        response.andExpect {
            status { isOk() }
            jsonPath("$.spaceId") {
                exists()
                isString()
                isNotEmpty()
            }
        }
    }

}