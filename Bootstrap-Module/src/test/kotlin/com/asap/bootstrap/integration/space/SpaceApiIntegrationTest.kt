package com.asap.bootstrap.integration.space

import com.asap.application.space.SpaceMockManager
import com.asap.bootstrap.IntegrationSupporter
import com.asap.bootstrap.space.dto.CreateSpaceRequest
import com.asap.bootstrap.space.dto.DeleteMultipleSpacesRequest
import com.asap.bootstrap.space.dto.UpdateSpaceNameRequest
import com.asap.bootstrap.space.dto.UpdateSpaceOrderRequest
import io.kotest.matchers.maps.haveValue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.haveLength
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
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
    lateinit var spaceMockManager: SpaceMockManager

    @Nested
    inner class GetMainSpace {
        @Test
        fun getMainSpaceId() {
            // given
            val userId = userMockManager.settingUser()
            val accessToken = jwtMockManager.generateAccessToken(userId)
            spaceMockManager.settingSpace(userId)
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
        fun getMainSpaceId_with_changedIndex() {
            // given
            val userId = userMockManager.settingUser()
            val accessToken = jwtMockManager.generateAccessToken(userId)
            val spaceIndexes =
                (0..3).map {
                    val spaceId = spaceMockManager.settingSpace(userId)
                    UpdateSpaceOrderRequest.SpaceOrder(spaceId, 3 - it)
                }
            mockMvc.put("/api/v1/spaces/order") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(UpdateSpaceOrderRequest(spaceIndexes))
                header("Authorization", "Bearer $accessToken")
            }
            // when
            val response =
                mockMvc.get("/api/v1/spaces/main") {
                    contentType = MediaType.APPLICATION_JSON
                    header("Authorization", "Bearer $accessToken")
                }

            // then
            response.andExpect {
                status { isOk() }
                jsonPath("$.spaceId") {
                    exists()
                    isString()
                    isNotEmpty()
                    value(spaceIndexes[3].spaceId)
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
    }

    @Test
    fun createSpace() {
        // given
        val userId = userMockManager.settingUser()
        val accessToken = jwtMockManager.generateAccessToken(userId)
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
        val userId = userMockManager.settingUser()
        val accessToken = jwtMockManager.generateAccessToken(userId)
        val spaceId = spaceMockManager.settingSpace(userId)
        val request =
            UpdateSpaceNameRequest(
                spaceName = "change space name",
            )
        // when
        val response =
            mockMvc.put("/api/v1/spaces/$spaceId/name") {
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
        for (i in 0..2) {
            spaceMockManager.settingSpace(userId)
        }
        // when
        val response =
            mockMvc.get("/api/v1/spaces") {
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

    @Nested
    @DisplayName("스페이스 삭제")
    inner class DeleteSpace {
        @Test
        fun deleteSpace() {
            // given
            val userId = userMockManager.settingUser()
            val accessToken = jwtMockManager.generateAccessToken(userId)
            val spaceId = spaceMockManager.settingSpace(userId)
            // when
            val response =
                mockMvc.delete("/api/v1/spaces/$spaceId") {
                    header("Authorization", "Bearer $accessToken")
                }
            // then
            response.andExpect {
                status { isOk() }
            }
        }

        @Test
        fun deleteSpace_with_reindexing_space_order() {
            // given
            val userId = userMockManager.settingUser()
            val accessToken = jwtMockManager.generateAccessToken(userId)
            val spaceIndexes =
                (0..3).map {
                    val spaceId = spaceMockManager.settingSpace(userId)
                    UpdateSpaceOrderRequest.SpaceOrder(spaceId, 3 - it)
                }
            val deleteSpaceId = spaceIndexes[1].spaceId
            mockMvc.delete("/api/v1/spaces/$deleteSpaceId") {
                header("Authorization", "Bearer $accessToken")
            }

            // when
            val response =
                mockMvc.get("/api/v1/spaces") {
                    header("Authorization", "Bearer $accessToken")
                    contentType = MediaType.APPLICATION_JSON
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
        fun deleteSpaces() {
            // given
            val userId = userMockManager.settingUser()
            val accessToken = jwtMockManager.generateAccessToken(userId)
            val spaceIds = (0..2).map { spaceMockManager.settingSpace(userId) }
            val request = DeleteMultipleSpacesRequest(spaceIds)
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
        fun deleteSpaces_with_reindexing_space_order() {
            // given
            val userId = userMockManager.settingUser()
            val accessToken = jwtMockManager.generateAccessToken(userId)
            val spaceIndexes =
                (0..3).map {
                    val spaceId = spaceMockManager.settingSpace(userId)
                    UpdateSpaceOrderRequest.SpaceOrder(spaceId, 3 - it)
                }
            val deleteSpaceIds = spaceIndexes.subList(1, 3).map { it.spaceId }
            val request = DeleteMultipleSpacesRequest(deleteSpaceIds)
            mockMvc.delete("/api/v1/spaces") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
                header("Authorization", "Bearer $accessToken")
            }

            // when
            val response =
                mockMvc.get("/api/v1/spaces") {
                    header("Authorization", "Bearer $accessToken")
                    contentType = MediaType.APPLICATION_JSON
                }
            // then
            response.andExpect {
                status { isOk() }
                jsonPath("$.spaces") {
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
                            haveValue(i)
                        }
                    }
                }
            }
        }
    }

    @Nested
    inner class UpdateSpaceOrder {
        @Test
        @DisplayName("space의 순서를 업데이트 한다.")
        fun updateSpaceOrder() {
            // given
            val userId = userMockManager.settingUser()
            val accessToken = jwtMockManager.generateAccessToken(userId)
            val spaceIndexes =
                (0..3).map {
                    val spaceId = spaceMockManager.settingSpace(userId)
                    UpdateSpaceOrderRequest.SpaceOrder(spaceId, 3 - it)
                }
            // when
            val response =
                mockMvc.put("/api/v1/spaces/order") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(UpdateSpaceOrderRequest(spaceIndexes))
                    header("Authorization", "Bearer $accessToken")
                }
            // then
            response.andExpect {
                status { isOk() }
            }
            spaceMockManager.getSpaceIndexes(userId) shouldBe
                spaceIndexes
                    .map { it.spaceId to it.index }
                    .sortedBy { it.second }
        }

        @Test
        @DisplayName("유효하지 않은 spaceId로 업데이트 요청시 400 에러를 반환한다.")
        fun updateSpaceOrder_with_invalid_spaceId() {
            // given
            val userId = userMockManager.settingUser()
            val accessToken = jwtMockManager.generateAccessToken(userId)
            val spaceIndexes =
                (0..3)
                    .map {
                        val spaceId = spaceMockManager.settingSpace(userId)
                        UpdateSpaceOrderRequest.SpaceOrder(spaceId, 3 - it)
                    }.toMutableList()
            val invalidSpaceId = UUID.randomUUID().toString()
            spaceIndexes[0] = UpdateSpaceOrderRequest.SpaceOrder(invalidSpaceId, spaceIndexes[0].index)
            // when
            val response =
                mockMvc.put("/api/v1/spaces/order") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(UpdateSpaceOrderRequest(spaceIndexes))
                    header("Authorization", "Bearer $accessToken")
                }
            // then
            response.andExpect {
                status { isBadRequest() }
            }
        }

        @Test
        @DisplayName("유효하지 않은 index로 업데이트 요청시 400 에러를 반환한다.")
        fun updateSpaceOrderFail_with_Invalid_Index() {
            // given
            val userId = userMockManager.settingUser()
            val accessToken = jwtMockManager.generateAccessToken(userId)
            val spaceIndexes =
                (0..3)
                    .map {
                        val spaceId = spaceMockManager.settingSpace(userId)
                        UpdateSpaceOrderRequest.SpaceOrder(spaceId, 3 - it)
                    }.toMutableList()
            spaceIndexes[0] = UpdateSpaceOrderRequest.SpaceOrder(spaceIndexes[0].spaceId, 4)
            // when
            val response =
                mockMvc.put("/api/v1/spaces/order") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(UpdateSpaceOrderRequest(spaceIndexes))
                    header("Authorization", "Bearer $accessToken")
                }
            // then
            response.andExpect {
                status { isBadRequest() }
            }
        }

        @Test
        @DisplayName("사용자의 space보다 많은 index로 업데이트 요청시 400 에러를 반환한다.")
        fun updateSpaceOrderFail_with_over_Index_exists_space() {
            // given
            val userId = userMockManager.settingUser()
            val accessToken = jwtMockManager.generateAccessToken(userId)
            val spaceIndexes =
                (0..3)
                    .map {
                        val spaceId = spaceMockManager.settingSpace(userId)
                        UpdateSpaceOrderRequest.SpaceOrder(spaceId, 3 - it)
                    }.toMutableList()
            spaceIndexes.add(UpdateSpaceOrderRequest.SpaceOrder(UUID.randomUUID().toString(), 4))
            // when
            val response =
                mockMvc.put("/api/v1/spaces/order") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(UpdateSpaceOrderRequest(spaceIndexes))
                    header("Authorization", "Bearer $accessToken")
                }
            // then
            response.andExpect {
                status { isBadRequest() }
            }
        }

        @Test
        @DisplayName("사용자의 space보다 적은 index로 업데이트 요청시 400 에러를 반환한다.")
        fun updateSpaceOrderFail_with_less_Index_exists_space() {
            // given
            val userId = userMockManager.settingUser()
            val accessToken = jwtMockManager.generateAccessToken(userId)
            val spaceIndexes =
                (0..3)
                    .map {
                        val spaceId = spaceMockManager.settingSpace(userId)
                        UpdateSpaceOrderRequest.SpaceOrder(spaceId, 3 - it)
                    }.toMutableList()
            spaceIndexes.removeLast()
            // when
            val response =
                mockMvc.put("/api/v1/spaces/order") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(UpdateSpaceOrderRequest(spaceIndexes))
                    header("Authorization", "Bearer $accessToken")
                }
            // then
            response.andExpect {
                status { isBadRequest() }
            }
        }

        @Test
        @DisplayName("인덱스를 중복해서 요청하면 400 에러를 반환한다.")
        fun updateSpaceOrderFail_with_duplicate_Index() {
            // given
            val userId = userMockManager.settingUser()
            val accessToken = jwtMockManager.generateAccessToken(userId)
            val spaceIndexes =
                (0..3)
                    .map {
                        val spaceId = spaceMockManager.settingSpace(userId)
                        UpdateSpaceOrderRequest.SpaceOrder(spaceId, 3 - it)
                    }.toMutableList()
            spaceIndexes[0] = UpdateSpaceOrderRequest.SpaceOrder(spaceIndexes[0].spaceId, 0)
            // when
            val response =
                mockMvc.put("/api/v1/spaces/order") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(UpdateSpaceOrderRequest(spaceIndexes))
                    header("Authorization", "Bearer $accessToken")
                }
            // then
            response.andExpect {
                status { isBadRequest() }
            }
        }
    }

    @Test
    fun getSpace() {
        // given
        val userId = userMockManager.settingUser()
        val accessToken = jwtMockManager.generateAccessToken(userId)
        val spaceId = spaceMockManager.settingSpace(userId)
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
}
