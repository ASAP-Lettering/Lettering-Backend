package com.asap.bootstrap.acceptance.space.controller

import com.asap.application.space.port.`in`.*
import com.asap.bootstrap.AcceptanceSupporter
import com.asap.bootstrap.web.space.dto.CreateSpaceRequest
import com.asap.bootstrap.web.space.dto.DeleteMultipleSpacesRequest
import com.asap.bootstrap.web.space.dto.UpdateSpaceNameRequest
import com.asap.bootstrap.web.space.dto.UpdateSpaceOrderRequest
import io.kotest.matchers.string.haveLength
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put

class SpaceControllerTest : AcceptanceSupporter() {
    @MockBean
    lateinit var getMainSpaceUsecase: GetMainSpaceUsecase

    @MockBean
    lateinit var updateSpaceNameUsecase: UpdateSpaceNameUsecase

    @MockBean
    lateinit var spaceCreateUsecase: CreateSpaceUsecase

    @MockBean
    lateinit var getSpaceUsecase: GetSpaceUsecase

    @MockBean
    lateinit var deleteSpaceUsecase: DeleteSpaceUsecase

    @MockBean
    lateinit var updateSpaceUsecase: UpdateSpaceUsecase

    @Test
    fun getMainSpaceId() {
        // given
        val accessToken = jwtMockManager.generateAccessToken("userId")
        BDDMockito
            .given(
                getMainSpaceUsecase.get(
                    GetMainSpaceUsecase.Query("userId"),
                ),
            ).willReturn(GetMainSpaceUsecase.Response("spaceId", "username", 0, "spaceName"))
        // when
        val response =
            mockMvc.get("/api/v1/spaces/main") {
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
            jsonPath("$.username") {
                exists()
                isString()
                isNotEmpty()
            }
            jsonPath("$.templateType") {
                exists()
                isNumber()
            }
            jsonPath("$.spaceName") {
                exists()
                isString()
                isNotEmpty()
            }
        }
    }

    @Test
    fun createSpace() {
        // given
        val accessToken = jwtMockManager.generateAccessToken("userId")
        val request =
            CreateSpaceRequest(
                spaceName = "spaceName",
                templateType = 0,
            )
        // when
        val response =
            mockMvc.post("/api/v1/spaces") {
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
        val accessToken = jwtMockManager.generateAccessToken("userId")
        val request =
            UpdateSpaceNameRequest(
                spaceName = "spaceName",
            )
        // when
        val response =
            mockMvc.put("/api/v1/spaces/spaceId/name") {
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
        val userId = userMockManager.settingUser()
        val accessToken = jwtMockManager.generateAccessToken(userId)
        BDDMockito
            .given(
                getSpaceUsecase.getAll(
                    GetSpaceUsecase.GetAllQuery(userId),
                ),
            ).willReturn(
                GetSpaceUsecase.GetAllResponse(
                    listOf(
                        GetSpaceUsecase.SpaceDetail(
                            spaceName = "spaceName",
                            letterCount = 0,
                            isMainSpace = true,
                            spaceIndex = 0,
                            spaceId = "spaceId",
                        ),
                        GetSpaceUsecase.SpaceDetail(
                            spaceName = "spaceName",
                            letterCount = 0,
                            isMainSpace = false,
                            spaceIndex = 1,
                            spaceId = "spaceId",
                        ),
                    ),
                ),
            )
        // when
        val response =
            mockMvc.get("/api/v1/spaces") {
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
        val accessToken = jwtMockManager.generateAccessToken()
        val spaceId = "spaceId"
        // when
        val response =
            mockMvc.delete("/api/v1/spaces/{spaceId}", spaceId) {
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
        val accessToken = jwtMockManager.generateAccessToken()
        val request = DeleteMultipleSpacesRequest(listOf("spaceId1", "spaceId2"))
        // when
        val response =
            mockMvc.delete("/api/v1/spaces") {
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
        val accessToken = jwtMockManager.generateAccessToken()
        val request =
            UpdateSpaceOrderRequest(
                orders =
                    listOf(
                        UpdateSpaceOrderRequest.SpaceOrder(spaceId = "spaceId1", index = 0),
                        UpdateSpaceOrderRequest.SpaceOrder(spaceId = "spaceId2", index = 1),
                    ),
            )
        // when
        val response =
            mockMvc.put("/api/v1/spaces/order") {
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
    fun getSpace() {
        // given
        val userId = userMockManager.settingUser()
        val accessToken = jwtMockManager.generateAccessToken(userId)
        val spaceId = "spaceId"
        BDDMockito
            .given(
                getSpaceUsecase.get(
                    GetSpaceUsecase.GetQuery(userId, spaceId),
                ),
            ).willReturn(
                GetSpaceUsecase.GetResponse(
                    spaceId = "spaceId",
                    spaceName = "spaceName",
                    templateType = 0,
                ),
            )
        // when
        val response =
            mockMvc.get("/api/v1/spaces/$spaceId") {
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
            jsonPath("$.spaceName") {
                exists()
                isString()
                isNotEmpty()
            }
            jsonPath("$.templateType") {
                exists()
                isNumber()
            }
        }
    }

    @Test
    fun updateSpaceMain() {
        // given
        val accessToken = jwtMockManager.generateAccessToken()
        val spaceId = "spaceId"
        // when
        val response =
            mockMvc.put("/api/v1/spaces/$spaceId/main") {
                header("Authorization", "Bearer $accessToken")
            }
        // then
        response.andExpect {
            status { isOk() }
        }
    }
}
