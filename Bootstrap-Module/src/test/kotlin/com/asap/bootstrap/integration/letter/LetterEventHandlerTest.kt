package com.asap.bootstrap.integration.letter

import com.asap.application.letter.LetterMockManager
import com.asap.application.user.event.UserEvent
import com.asap.bootstrap.IntegrationSupporter
import com.asap.bootstrap.letter.handler.LetterEventHandler
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.get

class LetterEventHandlerTest : IntegrationSupporter() {
    @Autowired
    private lateinit var letterEventHandler: LetterEventHandler

    @Autowired
    private lateinit var letterMockManager: LetterMockManager

    @Nested
    inner class OnUserDeleted {
        @Test
        fun `delete all independent letters when user is deleted`() {
            // given
            val user = userMockManager.settingUserWithUserDomain()
            val event = UserEvent.UserDeletedEvent(user)

            (0..1).forEach { _ ->
                letterMockManager.generateMockIndependentLetter(
                    senderId = user.id.value,
                    receiverId = user.id.value,
                    senderName = "sender",
                )
            }

            // when
            letterEventHandler.onUserDeleted(event)

            val response =
                mockMvc.get("/api/v1/letters/independent") {
                    header("Authorization", "Bearer ${jwtMockManager.generateAccessToken(user.id.value)}")
                }

            // then
            response.andExpect {
                status { isOk() }
                jsonPath("$.content") {
                    isArray()
                    isEmpty()
                }
            }
        }

        @Test
        fun `delete all sender letters when user is deleted`() {
            // given
            val user = userMockManager.settingUserWithUserDomain()
            val event = UserEvent.UserDeletedEvent(user)

            (0..1).forEach { _ ->
                letterMockManager.generateMockSendLetter(
                    receiverName = "receiver",
                    senderId = user.id.value,
                )
            }

            // when
            letterEventHandler.onUserDeleted(event)

            val response =
                mockMvc.get("/api/v1/letters/send") {
                    header("Authorization", "Bearer ${jwtMockManager.generateAccessToken(user.id.value)}")
                }

            // then
            response.andExpect {
                status { isOk() }
                jsonPath("$.content") {
                    isArray()
                    isEmpty()
                }
            }
        }
    }
}
