package com.asap.bootstrap.acceptance.letter.controller

import com.asap.bootstrap.AcceptanceSupporter
import com.asap.bootstrap.letter.controller.LetterController
import com.asap.bootstrap.letter.dto.SendLetterRequest
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.post

@WebMvcTest(LetterController::class)
class LetterControllerTest: AcceptanceSupporter() {

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
        val accessToken = testJwtDataGenerator.generateAccessToken()
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