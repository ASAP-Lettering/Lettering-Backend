package com.asap.bootstrap.integration.letter

import com.asap.application.letter.LetterMockManager
import com.asap.application.space.SpaceMockManager
import com.asap.bootstrap.IntegrationSupporter
import com.asap.bootstrap.web.letter.dto.ModifyLetterRequest
import com.asap.bootstrap.web.letter.dto.MoveLetterToSpaceRequest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.put

class SpaceLetterApiIntegrationTest : IntegrationSupporter() {
    @Autowired
    lateinit var spaceMockManager: SpaceMockManager

    @Autowired
    lateinit var letterMockManager: LetterMockManager

    @Nested
    inner class MoveLetterToSpace {
        @Test
        fun moveLetterToSpace() {
            // given
            val userId = userMockManager.settingUser()
            val accessToken = jwtMockManager.generateAccessToken(userId)
            val spaceId = spaceMockManager.settingSpace(userId).id.value
            val independentLetterId =
                letterMockManager
                    .generateMockIndependentLetter(
                        receiverId = userId,
                        senderName = "senderName",
                    ).id.value
            val request = MoveLetterToSpaceRequest(spaceId)
            // when
            val response =
                mockMvc.put("/api/v1/spaces/letters/$independentLetterId") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                    header("Authorization", "Bearer $accessToken")
                }
            // then
            response.andExpect {
                status { isOk() }
            }
        }

        @Test
        fun moveLetterToSpace_not_found_in_letter_list() {
            // given
            val userId = userMockManager.settingUser()
            val accessToken = jwtMockManager.generateAccessToken(userId)
            val spaceId = spaceMockManager.settingSpace(userId).id.value
            val independentLetterId =
                letterMockManager
                    .generateMockIndependentLetter(
                        receiverId = userId,
                        senderName = "senderName",
                    ).id.value
            val request = MoveLetterToSpaceRequest(spaceId)
            // when
            mockMvc.put("/api/v1/spaces/letters/$independentLetterId") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
                header("Authorization", "Bearer $accessToken")
            }

            val response =
                mockMvc.get("/api/v1/letters/independent") {
                    contentType = MediaType.APPLICATION_JSON
                    header("Authorization", "Bearer $accessToken")
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
        fun moveLetterToSpace_inserted_first() {
            // given
            val userId = userMockManager.settingUser()
            val accessToken = jwtMockManager.generateAccessToken(userId)
            val spaceId = spaceMockManager.settingSpace(userId).id.value
            letterMockManager.generateMockSpaceLetter(
                receiverId = userId,
                senderName = "senderName",
                spaceId = spaceId,
            )
            val independentLetterId =
                letterMockManager
                    .generateMockIndependentLetter(
                        receiverId = userId,
                        senderName = "senderName",
                    ).id.value
            letterMockManager.generateMockSpaceLetter(
                receiverId = userId,
                senderName = "senderName",
                spaceId = spaceId,
            )
            val request = MoveLetterToSpaceRequest(spaceId)

            // when
            mockMvc.put("/api/v1/spaces/letters/$independentLetterId") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
                header("Authorization", "Bearer $accessToken")
            }

            val response =
                mockMvc.get("/api/v1/spaces/$spaceId/letters?page=0&size=10") {
                    contentType = MediaType.APPLICATION_JSON
                    header("Authorization", "Bearer $accessToken")
                }

            // then
            response.andExpect {
                status { isOk() }
                jsonPath("$.content") {
                    isArray()
                    jsonPath("$.content[0].letterId") {
                        value(independentLetterId)
                    }
                }
            }
        }
    }

    @Nested
    inner class MoveLetterToIndependent {
        @Test
        fun moveLetterToIndependentLetter() {
            // given
            val userId = userMockManager.settingUser()
            val accessToken = jwtMockManager.generateAccessToken(userId)
            val spaceId = spaceMockManager.settingSpace(userId).id.value
            val spaceLetterId =
                letterMockManager
                    .generateMockSpaceLetter(
                        receiverId = userId,
                        senderName = "senderName",
                        spaceId = spaceId,
                    ).id.value
            // when
            val response =
                mockMvc.put("/api/v1/spaces/letters/$spaceLetterId/independent") {
                    contentType = MediaType.APPLICATION_JSON
                    header("Authorization", "Bearer $accessToken")
                }
            // then
            response.andExpect {
                status { isOk() }
            }
        }

        @Test
        fun moveLetterToIndependentLetter_not_found_in_letter_list() {
            // given
            val userId = userMockManager.settingUser()
            val accessToken = jwtMockManager.generateAccessToken(userId)
            val spaceId = spaceMockManager.settingSpace(userId).id.value
            val spaceLetterId =
                letterMockManager
                    .generateMockSpaceLetter(
                        receiverId = userId,
                        senderName = "senderName",
                        spaceId = spaceId,
                    ).id.value
            // when
            mockMvc.put("/api/v1/spaces/letters/$spaceLetterId/independent") {
                contentType = MediaType.APPLICATION_JSON
                header("Authorization", "Bearer $accessToken")
            }

            val response =
                mockMvc.get("/api/v1/spaces/$spaceId/letters?page=0&size=10") {
                    contentType = MediaType.APPLICATION_JSON
                    header("Authorization", "Bearer $accessToken")
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

    @Test
    fun getAllSpaceLetters() {
        // given
        val userId = userMockManager.settingUser()
        val accessToken = jwtMockManager.generateAccessToken(userId)
        val spaceId = spaceMockManager.settingSpace(userId).id.value
        val letterIds =
            (0..30).map {
                val letter =
                    letterMockManager.generateMockSpaceLetter(
                        receiverId = userId,
                        senderName = "senderName$it",
                        spaceId = spaceId,
                    )
                return@map letter.id.value to letter.sender.senderName
            }
        val page = 1
        val size = 10
        // when
        val response =
            mockMvc.get("/api/v1/spaces/$spaceId/letters?page=$page&size=$size") {
                contentType = MediaType.APPLICATION_JSON
                header("Authorization", "Bearer $accessToken")
            }
        // then
        response.andExpect {
            status { isOk() }
            jsonPath("$.content") {
                isArray()
                (0..9).forEachIndexed { index, _ ->
                    val expectedIndex = letterIds.size - 1 - index - page * size
                    jsonPath("$.content[$index].senderName") {
                        value(letterIds[expectedIndex].second)
                    }
                    jsonPath("$.content[$index].letterId") {
                        value(letterIds[expectedIndex].first)
                    }
                }
            }
            jsonPath("$.totalElements") {
                isNumber()
            }
            jsonPath("$.totalPages") {
                isNumber()
            }
            jsonPath("$.size") {
                isNumber()
            }
            jsonPath("$.page") {
                isNumber()
            }
        }
    }

    @Nested
    inner class GetSpaceLetterDetail {
        @Test
        @DisplayName("편지 상세 조회 성공")
        fun getSpaceLetterDetail() {
            // given
            val userId = userMockManager.settingUser(username = "username")
            val senderId = userMockManager.settingUser(username = "senderUsername")
            val accessToken = jwtMockManager.generateAccessToken(userId)
            val spaceId = spaceMockManager.settingSpace(userId).id.value
            val letters =
                (0..3).map {
                    letterMockManager.generateMockSpaceLetter(
                        senderId = senderId,
                        receiverId = userId,
                        spaceId = spaceId,
                        senderName = "senderUsername",
                    )
                }
            val letterId = letters[1].id.value
            // when
            val response =
                mockMvc.get("/api/v1/spaces/letters/$letterId") {
                    contentType = MediaType.APPLICATION_JSON
                    header("Authorization", "Bearer $accessToken")
                }
            // then
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
                jsonPath("$.receiveDate") {
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
                        value(letters[0].id.value)
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
                        value(letters[2].id.value)
                    }
                    jsonPath("$.nextLetter.senderName") {
                        exists()
                        isString()
                    }
                }
            }
        }

        @Test
        @DisplayName("행성 내의 편지 수만 반환한다.")
        fun getSpaceLetterDetail_only_space_letter() {
            // given
            val userId = userMockManager.settingUser(username = "username")
            val senderId = userMockManager.settingUser(username = "senderUsername")
            val accessToken = jwtMockManager.generateAccessToken(userId)
            val spaceId = spaceMockManager.settingSpace(userId).id.value
            val letters =
                (0..3).map {
                    letterMockManager.generateMockSpaceLetter(
                        senderId = senderId,
                        receiverId = userId,
                        spaceId = spaceId,
                        senderName = "senderUsername",
                    )
                }

            (0..3).forEach {
                letterMockManager.generateMockIndependentLetter(
                    senderId = senderId,
                    receiverId = userId,
                    senderName = "senderUsername",
                )
            }
            val letterId = letters[1].id.value
            // when
            val response =
                mockMvc.get("/api/v1/spaces/letters/$letterId") {
                    contentType = MediaType.APPLICATION_JSON
                    header("Authorization", "Bearer $accessToken")
                }
            // then
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
                    value(4)
                }
                jsonPath("$.content") {
                    exists()
                    isString()
                    isNotEmpty()
                }
                jsonPath("$.receiveDate") {
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
                        value(letters[0].id.value)
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
                        value(letters[2].id.value)
                    }
                    jsonPath("$.nextLetter.senderName") {
                        exists()
                        isString()
                    }
                }
            }
        }
    }

    @Nested
    inner class DeleteSpaceLetter {
        @Test
        @DisplayName("공간 편지 삭제 성공")
        fun deleteSpaceLetter() {
            // given
            val userId = userMockManager.settingUser()
            val accessToken = jwtMockManager.generateAccessToken(userId)
            val spaceId = spaceMockManager.settingSpace(userId).id.value
            val spaceLetter =
                letterMockManager.generateMockSpaceLetter(
                    receiverId = userId,
                    senderName = "senderName",
                    spaceId = spaceId,
                )
            val letterId = spaceLetter.id.value
            // when
            mockMvc.delete("/api/v1/spaces/letters/$letterId") {
                contentType = MediaType.APPLICATION_JSON
                header("Authorization", "Bearer $accessToken")
            }

            val response =
                mockMvc.get("/api/v1/spaces/letters/$letterId") {
                    contentType = MediaType.APPLICATION_JSON
                    header("Authorization", "Bearer $accessToken")
                }

            // then
            response.andExpect {
                status { isNotFound() }
            }
        }

        @Test
        fun deleteSpaceLetter_not_found_in_letter_list() {
            // given
            val userId = userMockManager.settingUser()
            val accessToken = jwtMockManager.generateAccessToken(userId)
            val spaceId = spaceMockManager.settingSpace(userId).id.value
            val spaceLetter =
                letterMockManager.generateMockSpaceLetter(
                    receiverId = userId,
                    senderName = "senderName",
                    spaceId = spaceId,
                )
            val letterId = spaceLetter.id.value
            // when
            mockMvc.delete("/api/v1/spaces/letters/$letterId") {
                contentType = MediaType.APPLICATION_JSON
                header("Authorization", "Bearer $accessToken")
            }

            val response =
                mockMvc.get("/api/v1/spaces/$spaceId/letters?page=0&size=10") {
                    contentType = MediaType.APPLICATION_JSON
                    header("Authorization", "Bearer $accessToken")
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

    @Test
    fun updateSpaceLetter() {
        // given
        val userId = userMockManager.settingUser()
        val accessToken = jwtMockManager.generateAccessToken(userId)
        val spaceId = spaceMockManager.settingSpace(userId).id.value
        val spaceLetter =
            letterMockManager.generateMockSpaceLetter(
                receiverId = userId,
                senderName = "senderName",
                spaceId = spaceId,
            )
        val letterId = spaceLetter.id.value
        val request =
            ModifyLetterRequest(
                content = "updateContent",
                images = listOf("image1", "image2"),
                senderName = "updateSenderName",
                templateType = 1,
            )
        // when
        val response =
            mockMvc.put("/api/v1/spaces/letters/$letterId/content") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
                header("Authorization", "Bearer $accessToken")
            }
        // then
        response.andExpect {
            status { isOk() }
        }
    }
}
