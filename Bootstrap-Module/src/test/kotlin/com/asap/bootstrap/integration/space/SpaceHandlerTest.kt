package com.asap.bootstrap.integration.space

import com.asap.application.space.SpaceMockManager
import com.asap.application.user.event.UserEvent
import com.asap.bootstrap.IntegrationSupporter
import com.asap.bootstrap.web.space.handler.SpaceEventHandler
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.get

class SpaceHandlerTest : IntegrationSupporter() {
    @Autowired
    private lateinit var spaceEventHandler: SpaceEventHandler

    @Autowired
    private lateinit var spaceMockBean: SpaceMockManager

    @Test
    fun onUserDelete() {
        // given
        val user = userMockManager.settingUserWithUserDomain()
        val event = UserEvent.UserDeletedEvent(user)

        (0..1).forEach { _ ->
            spaceMockBean.settingSpace(user.id.value)
        }

        // when
        spaceEventHandler.onUserDeleted(event)

        val response =
            mockMvc.get("/api/v1/spaces") {
                header("Authorization", "Bearer ${jwtMockManager.generateAccessToken(user.id.value)}")
            }

        // then
        response.andExpect {
            status { isOk() }
            jsonPath("$.spaces") {
                isArray()
                isEmpty()
            }
        }
    }
}
