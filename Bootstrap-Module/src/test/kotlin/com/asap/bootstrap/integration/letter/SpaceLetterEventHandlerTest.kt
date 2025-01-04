package com.asap.bootstrap.integration.letter

import com.asap.application.letter.LetterMockManager
import com.asap.application.space.SpaceMockManager
import com.asap.bootstrap.IntegrationSupporter
import com.asap.bootstrap.web.space.dto.DeleteMultipleSpacesRequest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get

class SpaceLetterEventHandlerTest : IntegrationSupporter() {
    @Autowired
    private lateinit var spaceMockManager: SpaceMockManager

    @Autowired
    private lateinit var letterMockManager: LetterMockManager

    @Test
    @DisplayName("스페이스 삭제 시 스페이스 내부의 편지들은 모두 삭제된다.")
    fun deleteAllSpaceLetterBySpaceDeletedEvent() {
        // given
        val userId = userMockManager.settingUser()
        val space = spaceMockManager.settingSpace(userId)
        val accessToken = jwtMockManager.generateAccessToken(userId)

        (0..10).forEach { _ ->
            letterMockManager.generateMockSpaceLetter(
                spaceId = space.id.value,
                senderId = userId,
                receiverId = userId,
                senderName = "sender",
            )
        }

        // when
        mockMvc.delete("/api/v1/spaces/${space.id.value}") {
            header("Authorization", "Bearer $accessToken")
            contentType = MediaType.APPLICATION_JSON
        }

        val response =
            mockMvc.get("/api/v1/letters/count") {
                header("Authorization", "Bearer $accessToken")
                contentType = MediaType.APPLICATION_JSON
            }

        // then
        response.andExpect {
            status { isOk() }
            content {
                jsonPath("$.letterCount") {
                    value(0)
                }
                jsonPath("$.spaceCount") {
                    value(0)
                }
            }
        }
    }

    @Test
    fun deleteAllSpaceLettersByMultipleSpaceDeletedEvent() {
        // given
        val userId = userMockManager.settingUser()
        val spaces =
            (0..2).map {
                spaceMockManager.settingSpace(userId).apply {
                    (0..<10).forEach { _ ->
                        letterMockManager.generateMockSpaceLetter(
                            spaceId = this.id.value,
                            senderId = userId,
                            receiverId = userId,
                            senderName = "sender",
                        )
                    }
                }
            }
        val accessToken = jwtMockManager.generateAccessToken(userId)

        val request =
            DeleteMultipleSpacesRequest(
                spaceIds = (0..1).map { spaces[it].id.value },
            )

        // when

        mockMvc.delete("/api/v1/spaces") {
            header("Authorization", "Bearer $accessToken")
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }

        val response =
            mockMvc.get("/api/v1/letters/count") {
                header("Authorization", "Bearer $accessToken")
                contentType = MediaType.APPLICATION_JSON
            }

        // then
        response.andExpect {
            status { isOk() }
            content {
                jsonPath("$.letterCount") {
                    value(10)
                }
                jsonPath("$.spaceCount") {
                    value(1)
                }
            }
        }
    }
}
