package com.asap.bootstrap.integration.letter

import com.asap.application.letter.LetterMockManager
import com.asap.application.space.SpaceMockManager
import com.asap.application.user.UserMockManager
import com.asap.bootstrap.IntegrationSupporter
import com.asap.bootstrap.letter.dto.MoveLetterToSpaceRequest
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.put

class SpaceLetterApiIntegrationTest: IntegrationSupporter() {

    @Autowired
    lateinit var userMockManager: UserMockManager

    @Autowired
    lateinit var spaceMockManager: SpaceMockManager

    @Autowired
    lateinit var letterMockManager: LetterMockManager

    @Test
    fun moveLetterToSpace() {
        //given
        val userId = userMockManager.settingUser()
        val accessToken = testJwtDataGenerator.generateAccessToken(userId)
        val spaceId = spaceMockManager.settingSpace(userId)
        val independentLetterId = letterMockManager.generateMockIndependentLetter(
            receiverId = userId,
            senderName = "senderName",
        )["letterId"] as String
        val request = MoveLetterToSpaceRequest(spaceId)
        //when
        val response = mockMvc.put("/api/v1/spaces/letters/$independentLetterId") {
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
        val userId = userMockManager.settingUser()
        val accessToken = testJwtDataGenerator.generateAccessToken(userId)
        val spaceId = spaceMockManager.settingSpace(userId)
        val spaceLetterId = letterMockManager.generateMockSpaceLetter(
            receiverId = userId,
            senderName = "senderName",
            spaceId = spaceId
        )["letterId"] as String
        //when
        val response = mockMvc.put("/api/v1/spaces/letters/$spaceLetterId/independent") {
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
        val userId = userMockManager.settingUser()
        val accessToken = testJwtDataGenerator.generateAccessToken(userId)
        val spaceId = spaceMockManager.settingSpace(userId)
        val letterIds = (0..30).map{
            val letter = letterMockManager.generateMockSpaceLetter(
                receiverId = userId,
                senderName = "senderName$it",
                spaceId = spaceId
            )
            return@map letter["letterId"] as String to letter["senderName"] as String
        }
        val page=1
        val size=10
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
                (0..9).forEachIndexed{ index, _ ->
                    jsonPath("$.content[$index].senderName"){
                        value(letterIds[index+page*size].second)
                    }
                    jsonPath("$.content[$index].letterId"){
                        value(letterIds[index+page*size].first)
                    }
                }
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



    @Nested
    inner class GetSpaceLetterDetail {
        @Test
        @DisplayName("편지 상세 조회 성공")
        fun getSpaceLetterDetail() {
            //given
            val userId = userMockManager.settingUser(username = "username")
            val senderId = userMockManager.settingUser(username = "senderUsername")
            val accessToken = testJwtDataGenerator.generateAccessToken(userId)
            val spaceId = spaceMockManager.settingSpace(userId)
            val letters = (0..3).map {
                letterMockManager.generateMockSpaceLetter(
                    senderId = senderId,
                    receiverId = userId,
                    spaceId = spaceId,
                    senderName = "senderUsername"
                )
            }
            val letterId = letters[1]["letterId"] as String
            //when
            val response = mockMvc.get("/api/v1/spaces/letters/$letterId") {
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
                jsonPath("$.images") {
                    exists()
                    isArray()
                }
                jsonPath("$.templateType") {
                    exists()
                    isNumber()
                }
                jsonPath("$.prevLetter") {
                    exists()
                    jsonPath("$.prevLetter.letterId") {
                        exists()
                        isString()
                        value(letters[0]["letterId"] as String)
                    }
                    jsonPath("$.prevLetter.senderName") {
                        exists()
                        isString()
                    }
                }
                jsonPath("$.nextLetter") {
                    exists()
                    jsonPath("$.nextLetter.letterId") {
                        exists()
                        isString()
                        value(letters[2]["letterId"] as String)
                    }
                    jsonPath("$.nextLetter.senderName") {
                        exists()
                        isString()
                    }
                }
            }
        }
    }

    @Test
    fun deleteSpaceLetter(){
        //given
        val userId = userMockManager.settingUser()
        val accessToken = testJwtDataGenerator.generateAccessToken()
        val spaceId = spaceMockManager.settingSpace(userId)
        val spaceLetter = letterMockManager.generateMockSpaceLetter(
            receiverId = userId,
            senderName = "senderName",
            spaceId = spaceId
        )
        val letterId = spaceLetter["letterId"] as String
        //when
        val response = mockMvc.delete("/api/v1/spaces/letters/$letterId") {
            contentType = MediaType.APPLICATION_JSON
            header("Authorization", "Bearer $accessToken")
        }
        //then
        response.andExpect {
            status { isOk() }
        }
        letterMockManager.isExistSpaceLetter(letterId, userId) shouldBe false
    }
}