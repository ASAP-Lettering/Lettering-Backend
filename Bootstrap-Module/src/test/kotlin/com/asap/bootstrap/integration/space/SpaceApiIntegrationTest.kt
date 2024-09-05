package com.asap.bootstrap.integration.space

import com.asap.application.space.SpaceMockManager
import com.asap.application.user.UserMockManager
import com.asap.bootstrap.IntegrationSupporter
import com.asap.bootstrap.space.dto.CreateSpaceRequest
import com.asap.security.jwt.TestJwtDataGenerator
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.util.*


class SpaceApiIntegrationTest : IntegrationSupporter() {

    @Autowired
    lateinit var testJwtDataGenerator: TestJwtDataGenerator

    @Autowired
    lateinit var spaceMockManager: SpaceMockManager
    @Autowired
    lateinit var userMockManager: UserMockManager

    @Test
    fun getMainSpaceId() {
        // when
        val userId = UUID.randomUUID().toString()
        userMockManager.settingUser(userId)
        val accessToken = testJwtDataGenerator.generateAccessToken(userId)
        spaceMockManager.settingSpace(userId)
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

    @Test
    fun createSpace() {
        // given
        val userId = UUID.randomUUID().toString()
        userMockManager.settingUser(userId)
        val accessToken = testJwtDataGenerator.generateAccessToken(userId)
        val request = CreateSpaceRequest(
            spaceName = "spaceName",
            templateType = 0
        )
        // when
        val response = mockMvc.post("/api/v1/spaces") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
            header("Authorization", "Bearer $accessToken")
        }
        // then
        response.andExpect {
            status { isOk() }
        }
    }

}