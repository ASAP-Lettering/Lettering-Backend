package com.asap.bootstrap.acceptance.letter.controller

import com.asap.application.letter.port.`in`.GenerateDraftKeyUsecase
import com.asap.bootstrap.acceptance.letter.LetterAcceptanceSupporter
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.springframework.test.web.servlet.post

class DraftLetterControllerTest : LetterAcceptanceSupporter() {
    @Test
    fun `get draft key`() {
        // given
        val accessToken = testJwtDataGenerator.generateAccessToken()

        BDDMockito
            .given(generateDraftKeyUsecase.command(GenerateDraftKeyUsecase.Command("userId")))
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
}
