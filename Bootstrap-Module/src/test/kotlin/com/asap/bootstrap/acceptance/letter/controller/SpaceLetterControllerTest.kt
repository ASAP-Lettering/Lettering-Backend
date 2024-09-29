package com.asap.bootstrap.acceptance.letter.controller

import com.asap.application.letter.port.`in`.GetSpaceLetterDetailUsecase
import com.asap.application.letter.port.`in`.GetSpaceLettersUsecase
import com.asap.bootstrap.acceptance.letter.LetterAcceptanceSupporter
import com.asap.bootstrap.letter.dto.MoveLetterToSpaceRequest
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.put
import java.time.LocalDate

class SpaceLetterControllerTest : LetterAcceptanceSupporter() {
    @Test
    fun moveLetterToSpace() {
        // given
        val accessToken = testJwtDataGenerator.generateAccessToken()
        val request = MoveLetterToSpaceRequest("spaceId")
        val letterId = "letterId"
        // when
        val response =
            mockMvc.put("/api/v1/spaces/letters/$letterId") {
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
    fun moveLetterToIndependentLetter() {
        // given
        val accessToken = testJwtDataGenerator.generateAccessToken()
        val letterId = "letterId"
        // when
        val response =
            mockMvc.put("/api/v1/spaces/letters/$letterId/independent") {
                contentType = MediaType.APPLICATION_JSON
                header("Authorization", "Bearer $accessToken")
            }
        // then
        response.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun getAllSpaceLetters() {
        // given
        val accessToken = testJwtDataGenerator.generateAccessToken()
        val spaceId = "spaceId"
        val page = 0
        val size = 10
        val pageResponse =
            GetSpaceLettersUsecase.Response(
                letters =
                    listOf(
                        GetSpaceLettersUsecase.LetterInfo(
                            senderName = "senderName",
                            letterId = "letterId",
                        ),
                    ),
                total = 1,
                page = 0,
                size = 10,
                totalPages = 1,
            )
        BDDMockito
            .given(getSpaceLettersUsecase.get(GetSpaceLettersUsecase.Query(page, size, spaceId, "userId")))
            .willReturn(pageResponse)
        // when
        val response =
            mockMvc.get("/api/v1/spaces/$spaceId/letters?page=$page&size=$size") {
                contentType = MediaType.APPLICATION_JSON
                header("Authorization", "Bearer $accessToken")
            }
        // then
        response.andExpect {
            status { isOk() }
            jsonPath("$.content") {
                isArray()
            }
            jsonPath("$.totalElements") {
                isNumber()
            }
            jsonPath("$.totalPages") {
                isNumber()
            }
            jsonPath("$.size") {
                isNumber()
            }
            jsonPath("$.page") {
                isNumber()
            }
        }
    }

    @Test
    fun getLetterDetail() {
        // given
        val accessToken = testJwtDataGenerator.generateAccessToken()
        val details =
            GetSpaceLetterDetailUsecase.Response(
                senderName = "senderName",
                spaceName = "spaceName",
                letterCount = 1,
                content = "content",
                sendDate = LocalDate.now(),
                images = listOf("images"),
                templateType = 1,
                prevLetter = GetSpaceLetterDetailUsecase.NearbyLetter("prevLetterId", "prevSenderName"),
                nextLetter = GetSpaceLetterDetailUsecase.NearbyLetter("nextLetterId", "nextSenderName"),
            )
        BDDMockito
            .given(
                getSpaceLetterDetailUsecase.get(
                    GetSpaceLetterDetailUsecase.Query(
                        letterId = "letterId",
                        userId = "userId",
                    ),
                ),
            ).willReturn(details)
        // when
        val response =
            mockMvc.get("/api/v1/spaces/letters/{letterId}", "letterId") {
                contentType = MediaType.APPLICATION_JSON
                header("Authorization", "Bearer $accessToken")
            }
        // then
        response.andExpect {
            status { isOk() }
            jsonPath("$.senderName") {
                exists()
                isString()
                isNotEmpty()
            }
            jsonPath("$.spaceName") {
                exists()
                isString()
                isNotEmpty()
            }
            jsonPath("$.letterCount") {
                exists()
                isNumber()
            }
            jsonPath("$.content") {
                exists()
                isString()
                isNotEmpty()
            }
            jsonPath("$.sendDate") {
                exists()
                isString()
                isNotEmpty()
            }
            jsonPath("$.templateType") {
                exists()
                isNumber()
            }
            jsonPath("$.images") {
                exists()
                isArray()
            }
            jsonPath("$.prevLetter") {
                exists()
                jsonPath("$.prevLetter.letterId") {
                    exists()
                    isString()
                    isNotEmpty()
                }
                jsonPath("$.prevLetter.senderName") {
                    exists()
                    isString()
                    isNotEmpty()
                }
            }
            jsonPath("$.nextLetter") {
                exists()
                jsonPath("$.nextLetter.letterId") {
                    exists()
                    isString()
                    isNotEmpty()
                }
                jsonPath("$.nextLetter.senderName") {
                    exists()
                    isString()
                    isNotEmpty()
                }
            }
        }
    }

    @Test
    fun deleteSpaceLetter() {
        // given
        val accessToken = testJwtDataGenerator.generateAccessToken()
        val letterId = "letterId"
        // when
        val response =
            mockMvc.delete("/api/v1/spaces/letters/$letterId") {
                contentType = MediaType.APPLICATION_JSON
                header("Authorization", "Bearer $accessToken")
            }
        // then
        response.andExpect {
            status { isOk() }
        }
    }
}
