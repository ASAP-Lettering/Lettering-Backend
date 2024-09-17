package com.asap.bootstrap.integration.letter

import com.asap.application.letter.LetterMockManager
import com.asap.application.space.SpaceMockManager
import com.asap.application.user.UserMockManager
import com.asap.bootstrap.IntegrationSupporter
import com.asap.bootstrap.letter.dto.MoveLetterToSpaceRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
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
}