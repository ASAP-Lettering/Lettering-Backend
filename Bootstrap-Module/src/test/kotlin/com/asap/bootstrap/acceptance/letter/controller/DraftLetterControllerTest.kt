package com.asap.bootstrap.acceptance.letter.controller

import com.asap.application.letter.port.`in`.GenerateDraftKeyUsecase
import com.asap.application.letter.port.`in`.GetDraftLetterUsecase
import com.asap.bootstrap.acceptance.letter.LetterAcceptanceSupporter
import com.asap.bootstrap.letter.dto.UpdateDraftLetterRequest
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.time.LocalDateTime

class DraftLetterControllerTest : LetterAcceptanceSupporter() {
    @Test
    fun `get draft key`() {
        // given
        val userId = userMockManager.settingUser()
        val accessToken = jwtMockManager.generateAccessToken(userId)

        BDDMockito
            .given(generateDraftKeyUsecase.command(GenerateDraftKeyUsecase.Command(userId)))
            .willReturn(GenerateDraftKeyUsecase.Response("draftId"))

        // when
        val response =
            mockMvc.post("/api/v1/letters/drafts/key") {
                header("Authorization", "Bearer $accessToken")
            }

        // then
        response.andExpect {
            status { isOk() }
            jsonPath("$.draftId") { isString() }
        }
    }

    @Test
    fun `update draft`() {
        // given
        val userId = userMockManager.settingUser()
        val accessToken = jwtMockManager.generateAccessToken(userId)
        val request =
            UpdateDraftLetterRequest(
                content = "content",
                receiverName = "receiverName",
                images = listOf("image"),
            )
        // when
        val response =
            mockMvc.post("/api/v1/letters/drafts/draftId") {
                contentType = MediaType.APPLICATION_JSON
                header("Authorization", "Bearer $accessToken")
                content = objectMapper.writeValueAsString(request)
            }

        // then
        response.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `get all drafts`() {
        // given
        val userId = userMockManager.settingUser()
        val accessToken = jwtMockManager.generateAccessToken(userId)

        BDDMockito
            .given(getDraftLetterUsecase.getAll(GetDraftLetterUsecase.Query.All(userId)))
            .willReturn(
                GetDraftLetterUsecase.Response.All(
                    drafts =
                        listOf(
                            GetDraftLetterUsecase.Response.ByKey(
                                draftKey = "draftKey",
                                receiverName = "receiverName",
                                content = "content",
                                lastUpdated = LocalDateTime.now(),
                                images = listOf("image"),
                            ),
                        ),
                ),
            )

        // when
        val response =
            mockMvc.get("/api/v1/letters/drafts") {
                contentType = MediaType.APPLICATION_JSON
                header("Authorization", "Bearer $accessToken")
            }

        // then
        response.andExpect {
            status { isOk() }
            jsonPath("$.drafts[0].draftKey") { isString() }
            jsonPath("$.drafts[0].receiverName") { isString() }
            jsonPath("$.drafts[0].content") { isString() }
            jsonPath("$.drafts[0].lastUpdated") { isString() }
        }
    }

    @Test
    fun `get draft letter`() {
        // given
        val userId = userMockManager.settingUser()
        val accessToken = jwtMockManager.generateAccessToken(userId)

        BDDMockito
            .given(getDraftLetterUsecase.getByKey(GetDraftLetterUsecase.Query.ByKey("draftKey", userId)))
            .willReturn(
                GetDraftLetterUsecase.Response.ByKey(
                    draftKey = "draftKey",
                    receiverName = "receiverName",
                    content = "content",
                    lastUpdated = LocalDateTime.now(),
                    images = listOf("image"),
                ),
            )

        // when
        val response =
            mockMvc.get("/api/v1/letters/drafts/draftKey") {
                contentType = MediaType.APPLICATION_JSON
                header("Authorization", "Bearer $accessToken")
            }

        // then
        response.andExpect {
            status { isOk() }
            jsonPath("$.draftKey") { isString() }
            jsonPath("$.receiverName") { isString() }
            jsonPath("$.content") { isString() }
            jsonPath("$.images") {
                isArray()
            }
        }
    }

    @Test
    fun `get draft count`() {
        // given
        val userId = userMockManager.settingUser()
        val accessToken = jwtMockManager.generateAccessToken(userId)

        BDDMockito
            .given(getDraftLetterUsecase.count(GetDraftLetterUsecase.Query.All(userId)))
            .willReturn(GetDraftLetterUsecase.Response.Count(1))

        // when
        val response =
            mockMvc.get("/api/v1/letters/drafts/count") {
                contentType = MediaType.APPLICATION_JSON
                header("Authorization", "Bearer $accessToken")
            }

        // then
        response.andExpect {
            status { isOk() }
            jsonPath("$.count") { isNumber() }
        }
    }

    @Test
    fun `delete draft`() {
        // given
        val userId = userMockManager.settingUser()
        val accessToken = jwtMockManager.generateAccessToken(userId)

        // when
        val response =
            mockMvc.delete("/api/v1/letters/drafts/draftKey") {
                contentType = MediaType.APPLICATION_JSON
                header("Authorization", "Bearer $accessToken")
            }

        // then
        response.andExpect {
            status { isOk() }
        }
    }
}
