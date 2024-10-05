package com.asap.bootstrap.integration.letter

import com.asap.application.letter.LetterMockManager
import com.asap.bootstrap.IntegrationSupporter
import com.asap.bootstrap.letter.dto.UpdateDraftLetterRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.post

class DraftLetterApiIntegrationTest : IntegrationSupporter() {
    @Autowired
    private lateinit var letterMockManager: LetterMockManager

    @Test
    fun `get draft key`() {
        // given
        val userId = userMockManager.settingUser()
        val accessToken = testJwtDataGenerator.generateAccessToken(userId)
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
        val accessToken = testJwtDataGenerator.generateAccessToken(userId)
        val draftId = letterMockManager.generateMockDraftLetter(userId)
        val request =
            UpdateDraftLetterRequest(
                content = "content",
                receiverName = "receiverName",
                images = listOf("image"),
            )
        // when
        val response =
            mockMvc.post("/api/v1/letters/drafts/$draftId") {
                contentType = MediaType.APPLICATION_JSON
                header("Authorization", "Bearer $accessToken")
                content = objectMapper.writeValueAsString(request)
            }

        // then
        response.andExpect {
            status { isOk() }
        }
    }
}
