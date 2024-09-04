package com.asap.bootstrap.acceptance.space.controller

import com.asap.application.space.port.`in`.MainSpaceQueryUsecase
import com.asap.bootstrap.AcceptanceSupporter
import com.asap.bootstrap.space.controller.SpaceController
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.get

@WebMvcTest(SpaceController::class)
class SpaceControllerTest : AcceptanceSupporter() {

    @MockBean
    lateinit var mainSpaceQueryUsecase: MainSpaceQueryUsecase

    @Test
    fun getMainSpaceId() {
        // given
        BDDMockito.given(mainSpaceQueryUsecase.query()).willReturn(MainSpaceQueryUsecase.Response("spaceId"))
        // when
        val response = mockMvc.get("/api/v1/spaces/main")
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