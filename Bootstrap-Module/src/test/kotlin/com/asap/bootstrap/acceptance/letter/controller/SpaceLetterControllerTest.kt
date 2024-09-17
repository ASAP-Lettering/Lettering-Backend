package com.asap.bootstrap.acceptance.letter.controller

import com.asap.application.letter.port.`in`.GetSpaceLettersUsecase
import com.asap.application.letter.port.`in`.MoveLetterUsecase
import com.asap.bootstrap.AcceptanceSupporter
import com.asap.bootstrap.letter.controller.SpaceLetterController
import com.asap.bootstrap.letter.dto.MoveLetterToSpaceRequest
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.put

@WebMvcTest(SpaceLetterController::class)
class SpaceLetterControllerTest: AcceptanceSupporter() {

    @MockBean
    lateinit var moveLetterUsecase: MoveLetterUsecase

    @MockBean
    lateinit var getSpaceLettersUsecase: GetSpaceLettersUsecase

    @Test
    fun moveLetterToSpace() {
        //given
        val accessToken = testJwtDataGenerator.generateAccessToken()
        val request = MoveLetterToSpaceRequest("spaceId")
        val letterId = "letterId"
        //when
        val response = mockMvc.put("/api/v1/spaces/letters/$letterId") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
            header("Authorization", "Bearer $accessToken")
        }
        //then
        response.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun moveLetterToIndependentLetter() {
        //given
        val accessToken = testJwtDataGenerator.generateAccessToken()
        val letterId = "letterId"
        //when
        val response = mockMvc.put("/api/v1/spaces/letters/$letterId/independent") {
            contentType = MediaType.APPLICATION_JSON
            header("Authorization", "Bearer $accessToken")
        }
        //then
        response.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun getAllSpaceLetters() {
        //given
        val accessToken = testJwtDataGenerator.generateAccessToken()
        val spaceId = "spaceId"
        val page = 0
        val size = 10
        val pageResponse = GetSpaceLettersUsecase.Response(
            letters = listOf(
                GetSpaceLettersUsecase.LetterInfo(
                    senderName = "senderName",
                    letterId = "letterId"
                )
            ),
            total = 1,
            page = 0,
            size = 10,
            totalPages = 1
        )
        BDDMockito.given(getSpaceLettersUsecase.get(GetSpaceLettersUsecase.Query(page, size, spaceId, "userId")))
            .willReturn(pageResponse)
        //when
        val response = mockMvc.get("/api/v1/spaces/$spaceId/letters?page=$page&size=$size") {
            contentType = MediaType.APPLICATION_JSON
            header("Authorization", "Bearer $accessToken")
        }
        //then
        response.andExpect {
            status { isOk() }
            jsonPath("$.content"){
                isArray()
            }
            jsonPath("$.totalElements"){
                isNumber()
            }
            jsonPath("$.totalPages"){
                isNumber()
            }
            jsonPath("$.size"){
                isNumber()
            }
            jsonPath("$.page"){
                isNumber()
            }
        }
    }
}