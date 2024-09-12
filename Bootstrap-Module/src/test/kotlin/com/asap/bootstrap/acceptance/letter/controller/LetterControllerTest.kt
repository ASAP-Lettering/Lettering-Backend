package com.asap.bootstrap.acceptance.letter.controller

import com.asap.application.letter.port.`in`.GetVerifiedLetterUsecase
import com.asap.application.letter.port.`in`.SendLetterUsecase
import com.asap.application.letter.port.`in`.VerifyLetterAccessibleUsecase
import com.asap.bootstrap.AcceptanceSupporter
import com.asap.bootstrap.letter.controller.LetterController
import com.asap.bootstrap.letter.dto.LetterVerifyRequest
import com.asap.bootstrap.letter.dto.SendLetterRequest
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import java.time.LocalDate

@WebMvcTest(LetterController::class)
class LetterControllerTest: AcceptanceSupporter() {

    @MockBean
    lateinit var verifyLetterAccessibleUsecase: VerifyLetterAccessibleUsecase

    @MockBean
    lateinit var sendLetterUsecase: SendLetterUsecase

    @MockBean
    lateinit var getVerifiedLetterUsecase: GetVerifiedLetterUsecase


    @Test
    fun verifyLetter() {
        //given
        val accessToken = testJwtDataGenerator.generateAccessToken()
        val request = LetterVerifyRequest("letterCode")
        BDDMockito.given(
            verifyLetterAccessibleUsecase.verify(
                VerifyLetterAccessibleUsecase.Command(
                    letterCode = request.letterCode,
                    userId = "userId"
                )
            )
        ).willReturn(VerifyLetterAccessibleUsecase.Response("letterId"))
        //when
        val response = mockMvc.put("/api/v1/letters/verify") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
            header("Authorization", "Bearer $accessToken")
        }
        //then
        response.andExpect {
            status { isOk() }
            jsonPath("$.letterId") {
                exists()
                isString()
                isNotEmpty()
            }
        }
    }

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
        BDDMockito.given(
            sendLetterUsecase.send(
                SendLetterUsecase.Command(
                    userId = "userId",
                    receiverName = request.receiverName,
                    content = request.content,
                    images = request.images,
                    templateType = request.templateType,
                    draftId = request.draftId
                )
            )
        ).willReturn(SendLetterUsecase.Response("letterCode"))
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


    @Test
    fun getVerifiedLetter() {
        //given
        val accessToken = testJwtDataGenerator.generateAccessToken()
        val verifiedLetterInfoResponse = GetVerifiedLetterUsecase.Response(
            senderName = "sendName",
            content = "content",
            sendDate = LocalDate.now(),
            templateType = 1,
            images = listOf("images")
        )
        BDDMockito.given(
            getVerifiedLetterUsecase.receive(
                GetVerifiedLetterUsecase.Query(
                    letterId = "letterId",
                    userId = "userId"
                )
            )
        ).willReturn(verifiedLetterInfoResponse)
        //when
        val response = mockMvc.get("/api/v1/letters/{letterId}/verify", "letterId") {
            contentType = MediaType.APPLICATION_JSON
            header("Authorization", "Bearer $accessToken")
        }
        //then
        response.andExpect {
            status { isOk() }
            jsonPath("$.senderName") {
                exists()
                isString()
                isNotEmpty()
            }
            jsonPath("$.content") {
                exists()
                isString()
                isNotEmpty()
            }
            jsonPath("$.date") {
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
        }
    }
}