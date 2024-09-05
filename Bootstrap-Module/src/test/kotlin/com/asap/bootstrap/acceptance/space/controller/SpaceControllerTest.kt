package com.asap.bootstrap.acceptance.space.controller

import com.asap.application.space.port.`in`.MainSpaceQueryUsecase
import com.asap.application.space.port.`in`.SpaceCreateUsecase
import com.asap.bootstrap.AcceptanceSupporter
import com.asap.bootstrap.space.controller.SpaceController
import com.asap.bootstrap.space.dto.CreateSpaceRequest
import com.asap.security.jwt.JwtTestConfig
import com.asap.security.jwt.TestJwtDataGenerator
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@WebMvcTest(SpaceController::class)
@Import(JwtTestConfig::class)
class SpaceControllerTest : AcceptanceSupporter() {

    @MockBean
    lateinit var mainSpaceQueryUsecase: MainSpaceQueryUsecase

    @MockBean
    lateinit var spaceCreateUsecase: SpaceCreateUsecase

    @Autowired
    lateinit var testJwtDataGenerator: TestJwtDataGenerator

    @Test
    fun getMainSpaceId() {
        // given
        val accessToken = testJwtDataGenerator.generateAccessToken("userId")
        BDDMockito.given(
            mainSpaceQueryUsecase.get(
                MainSpaceQueryUsecase.Query("userId")
            )
        ).willReturn(MainSpaceQueryUsecase.Response("spaceId"))
        // when
        val response = mockMvc.get("/api/v1/spaces/main"){
            header("Authorization","Bearer $accessToken")
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
        // when
        val response = mockMvc.post("/api/v1/spaces") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
            header("Authorization","Bearer $accessToken")
        }
        // then
        response.andExpect {
            status { isOk() }
        }
    }
}