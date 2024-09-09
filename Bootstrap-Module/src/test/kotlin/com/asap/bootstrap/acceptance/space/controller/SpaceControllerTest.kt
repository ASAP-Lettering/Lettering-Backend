package com.asap.bootstrap.acceptance.space.controller

import com.asap.application.space.port.`in`.*
import com.asap.bootstrap.AcceptanceSupporter
import com.asap.bootstrap.space.controller.SpaceController
import com.asap.bootstrap.space.dto.CreateSpaceRequest
import com.asap.bootstrap.space.dto.DeleteMultipleSpacesRequest
import com.asap.bootstrap.space.dto.UpdateSpaceOrderRequest
import com.asap.security.jwt.JwtTestConfig
import com.asap.security.jwt.TestJwtDataGenerator
import io.kotest.matchers.string.haveLength
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put

@WebMvcTest(SpaceController::class)
@Import(JwtTestConfig::class)
class SpaceControllerTest : AcceptanceSupporter() {

    @MockBean
    lateinit var mainSpaceGetUsecase: MainSpaceGetUsecase

    @MockBean
    lateinit var spaceUpdateNameUsecase: SpaceUpdateNameUsecase

    @MockBean
    lateinit var spaceCreateUsecase: SpaceCreateUsecase

    @MockBean
    lateinit var spaceGetUsecase: SpaceGetUsecase

    @MockBean
    lateinit var spaceDeleteUsecase: SpaceDeleteUsecase

    @MockBean
    lateinit var spaceUpdateIndexUsecase: SpaceUpdateIndexUsecase

    @Autowired
    lateinit var testJwtDataGenerator: TestJwtDataGenerator

    @Test
    fun getMainSpaceId() {
        // given
        val accessToken = testJwtDataGenerator.generateAccessToken("userId")
        BDDMockito.given(
            mainSpaceGetUsecase.get(
                MainSpaceGetUsecase.Query("userId")
            )
        ).willReturn(MainSpaceGetUsecase.Response("spaceId"))
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
        val accessToken = testJwtDataGenerator.generateAccessToken("userId")
        val request = CreateSpaceRequest(
            spaceName = "spaceName",
            templateType = 0
        )
        BDDMockito.given(
            mainSpaceGetUsecase.get(
                MainSpaceGetUsecase.Query("userId")
            )
        ).willReturn(MainSpaceGetUsecase.Response("spaceId"))
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
        val accessToken = testJwtDataGenerator.generateAccessToken("userId")
        val request = CreateSpaceRequest(
            spaceName = "spaceName",
            templateType = 0
        )
        // when
        val response = mockMvc.put("/api/v1/spaces/spaceId/name") {
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
        val accessToken = testJwtDataGenerator.generateAccessToken()
        BDDMockito.given(
            spaceGetUsecase.getAll(
                SpaceGetUsecase.GetAllQuery("userId")
            )
        ).willReturn(
            SpaceGetUsecase.GetAllResponse(
                listOf(
                    SpaceGetUsecase.SpaceDetail(
                        spaceName = "spaceName",
                        letterCount = 0,
                        isMainSpace = true,
                        spaceIndex = 0,
                        spaceId = "spaceId"
                    ),
                    SpaceGetUsecase.SpaceDetail(
                        spaceName = "spaceName",
                        letterCount = 0,
                        isMainSpace = false,
                        spaceIndex = 1,
                        spaceId = "spaceId"
                    )
                )
            )
        )
        // when
        val response = mockMvc.get("/api/v1/spaces") {
            header("Authorization", "Bearer $accessToken")
        }

        // then
        response.andExpect {
            status { isOk() }
            jsonPath("$.spaces") {
                exists()
                isArray()
                isNotEmpty()
                haveLength(2)
                for (i in 0..1) {
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
                        value(i)
                    }
                }
            }
        }
    }


    @Test
    fun deleteSpace() {
        // given
        val accessToken = testJwtDataGenerator.generateAccessToken()
        val spaceId = "spaceId"
        // when
        val response = mockMvc.delete("/api/v1/spaces/{spaceId}", spaceId) {
            header("Authorization", "Bearer $accessToken")
        }
        // then

        response.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun deleteSpaces() {
        // given
        val accessToken = testJwtDataGenerator.generateAccessToken()
        val request = DeleteMultipleSpacesRequest(listOf("spaceId1", "spaceId2"))
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
    }

    @Test
    fun updateSpaceOrder() {
        // given
        val accessToken = testJwtDataGenerator.generateAccessToken()
        val request = UpdateSpaceOrderRequest(
            orders = listOf(
                UpdateSpaceOrderRequest.SpaceOrder(spaceId = "spaceId1", index = 0),
                UpdateSpaceOrderRequest.SpaceOrder(spaceId = "spaceId2", index = 1)
            )
        )
        // when
        val response = mockMvc.put("/api/v1/spaces/order") {
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