package com.asap.bootstrap.integration.letter

import com.asap.application.letter.port.`in`.GenerateDraftKeyUsecase
import com.asap.bootstrap.IntegrationSupporter
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.post

class DraftLetterApiIntegrationTest : IntegrationSupporter() {
    @Autowired
    private lateinit var generateDraftKeyUsecase: GenerateDraftKeyUsecase

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
}
