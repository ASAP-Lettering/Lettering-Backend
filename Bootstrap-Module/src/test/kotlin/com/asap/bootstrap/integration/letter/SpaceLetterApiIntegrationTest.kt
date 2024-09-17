package com.asap.bootstrap.integration.letter

import com.asap.application.letter.LetterMockManager
import com.asap.application.space.SpaceMockManager
import com.asap.application.user.UserMockManager
import com.asap.bootstrap.IntegrationSupporter
import com.asap.bootstrap.letter.dto.MoveLetterToSpaceRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
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
}