package com.asap.bootstrap.integration.space

import com.asap.application.space.SpaceMockManager
import com.asap.application.user.UserMockManager
import com.asap.bootstrap.IntegrationSupporter
import com.asap.bootstrap.space.dto.CreateSpaceRequest
import com.asap.bootstrap.space.dto.DeleteMultipleSpacesRequest
import com.asap.bootstrap.space.dto.UpdateSpaceNameRequest
import com.asap.security.jwt.TestJwtDataGenerator
import io.kotest.matchers.maps.haveValue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.haveLength
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
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
        // given
        val userId = UUID.randomUUID().toString()
        userMockManager.settingUser(userId)
        val accessToken = testJwtDataGenerator.generateAccessToken(userId)
        spaceMockManager.settingSpace(userId)
        // when
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

    @Test
    fun updateSpaceName() {
        // given
        val userId = UUID.randomUUID().toString()
        userMockManager.settingUser(userId)
        val accessToken = testJwtDataGenerator.generateAccessToken(userId)
        val spaceId = spaceMockManager.settingSpace(userId)
        val request = UpdateSpaceNameRequest(
            spaceName = "change space name"
        )
        // when
        val response = mockMvc.put("/api/v1/spaces/$spaceId/name") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
            header("Authorization", "Bearer $accessToken")
        }
        // then
        response.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun getSpaces() {
        // given
        val userId = UUID.randomUUID().toString()
        userMockManager.settingUser(userId)
        val accessToken = testJwtDataGenerator.generateAccessToken(userId)
        for (i in 0..2)
            spaceMockManager.settingSpace(userId)
        // when
        val response = mockMvc.get("/api/v1/spaces") {
            header("Authorization", "Bearer $accessToken")
        }
        // then
        response.andExpect {
            status { isOk() }
            jsonPath("$.spaces") {
                isArray()
                isNotEmpty()
                haveLength(3)
                for (i in 0..2) {
                    jsonPath("$.spaces[$i].spaceId") {
                        exists()
                        isString()
                        isNotEmpty()
                    }
                    jsonPath("$.spaces[$i].spaceName") {
                        exists()
                        isString()
                        isNotEmpty()
                    }
                    jsonPath("$.spaces[$i].letterCount") {
                        exists()
                        isNumber()
                    }
                    jsonPath("$.spaces[$i].isMainSpace") {
                        exists()
                        isBoolean()
                    }
                    jsonPath("$.spaces[$i].spaceIndex") {
                        exists()
                        isNumber()
                        haveValue(i)
                    }
                }
            }
        }
    }

    @Test
    fun deleteSpace() {
        // given
        val userId = UUID.randomUUID().toString()
        userMockManager.settingUser(userId)
        val accessToken = testJwtDataGenerator.generateAccessToken(userId)
        val spaceId = spaceMockManager.settingSpace(userId)
        // when
        val response = mockMvc.delete("/api/v1/spaces/$spaceId") {
            header("Authorization", "Bearer $accessToken")
        }
        // then
        response.andExpect {
            status { isOk() }
        }
        spaceMockManager.getSpaceCount(userId) shouldBe 0
    }

    @Test
    fun deleteSpaces() {
        // given
        val userId = UUID.randomUUID().toString()
        userMockManager.settingUser(userId)
        val accessToken = testJwtDataGenerator.generateAccessToken(userId)
        val spaceIds = (0..2).map { spaceMockManager.settingSpace(userId) }
        val request = DeleteMultipleSpacesRequest(spaceIds)
        // when
        val response = mockMvc.delete("/api/v1/spaces") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
            header("Authorization", "Bearer $accessToken")
        }
        // then
        response.andExpect {
            status { isOk() }
        }
        spaceMockManager.getSpaceCount(userId) shouldBe 0
    }

}