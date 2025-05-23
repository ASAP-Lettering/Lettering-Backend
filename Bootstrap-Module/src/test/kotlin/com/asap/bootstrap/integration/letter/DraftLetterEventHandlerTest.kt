package com.asap.bootstrap.integration.letter

import com.asap.application.letter.LetterMockManager
import com.asap.application.user.event.UserEvent
import com.asap.bootstrap.IntegrationSupporter
import com.asap.bootstrap.web.letter.handler.DraftLetterEventHandler
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.get

class DraftLetterEventHandlerTest(
    val draftLetterEventHandler: DraftLetterEventHandler,
    val letterMockManager: LetterMockManager,
) : IntegrationSupporter() {

    @Test
    fun onUserDelete() {
        // given
        val user = userMockManager.settingUserWithUserDomain()
        val event = UserEvent.UserDeletedEvent(user)

        (0..1).forEach {
            letterMockManager.generateMockDraftLetter(
                userId = user.id.value,
            )
        }

        // when
        draftLetterEventHandler.onUserDelete(event)

        val response =
            mockMvc.get("/api/v1/letters/drafts") {
                header("Authorization", "Bearer ${jwtMockManager.generateAccessToken(user.id.value)}")
            }

        // then
        response.andExpect {
            status { isOk() }
            jsonPath("$.drafts") {
                isArray()
                isEmpty()
            }
        }
    }
}
