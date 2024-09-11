package com.asap.bootstrap.integration.letter

import com.asap.application.user.UserMockManager
import com.asap.bootstrap.IntegrationSupporter
import com.asap.bootstrap.letter.dto.SendLetterRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.post

class LetterApiIntegrationTest: IntegrationSupporter() {

    @Autowired
    lateinit var userMockManager: UserMockManager

    @Test
    fun sendLetter(){
        //given
        val request = SendLetterRequest(
            receiverName = "receiverName",
            content = "content",
            images = listOf("images"),
            templateType = 1,
            draftId = "draftId"
        )
        val userId = userMockManager.settingUser()
        val accessToken = testJwtDataGenerator.generateAccessToken(userId)
        userMockManager.settingToken(accessToken)
        //when
        val response = mockMvc.post("/api/v1/letters/send") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
            header("Authorization","Bearer $accessToken")
        }
        //then
        response.andExpect {
            status { isOk() }
            jsonPath("$.letterCode") {
                exists()
                isString()
                isNotEmpty()
            }
        }
    }
}