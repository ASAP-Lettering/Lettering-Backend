package com.asap.bootstrap.integration.letter

import com.asap.application.letter.LetterMockManager
import com.asap.application.space.SpaceMockManager
import com.asap.bootstrap.IntegrationSupporter
import com.asap.bootstrap.web.letter.dto.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import java.time.LocalDateTime

class LetterApiIntegrationTest : IntegrationSupporter() {
    @Autowired
    lateinit var letterMockManager: LetterMockManager

    @Autowired
    lateinit var spaceMockManager: SpaceMockManager

    @Nested
    inner class LetterVerify {
        @Test
        @DisplayName("편지 열람 가능 검증 성공")
        fun verifyLetter() {
            // given
            val senderId = userMockManager.settingUser(username = "senderUsername")
            val userId = userMockManager.settingUser(username = "username")
            val accessToken = jwtMockManager.generateAccessToken(userId)
            val letterCode =
                letterMockManager.generateMockSendLetter("username", senderId = senderId).letterCode!!
            val request = LetterVerifyRequest(letterCode)
            // when
            val response =
                mockMvc.put("/api/v1/letters/verify") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                    header("Authorization", "Bearer $accessToken")
                }
            // then
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
        @DisplayName("편지가 존재하지 않음")
        fun verifyLetter_With_InvalidLetterCode() {
            // given
            val userId = userMockManager.settingUser(username = "username")
            val accessToken = jwtMockManager.generateAccessToken(userId)
            val request = LetterVerifyRequest("invalidLetterCode")
            // when
            val response =
                mockMvc.put("/api/v1/letters/verify") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                    header("Authorization", "Bearer $accessToken")
                }
            // then
            response.andExpect {
                status { isNotFound() }
                jsonPath("$.code") {
                    value("LETTER-001")
                }
            }
        }

        @Test
        @DisplayName("해당 사용자는 편지 열람 권한이 없음")
        fun verifyLetter_With_InvalidUser() {
            // given
            val senderId = userMockManager.settingUser(username = "senderUsername")
            val userId = userMockManager.settingUser(username = "username")
            val accessToken = jwtMockManager.generateAccessToken(userId)
            val letterCode =
                letterMockManager.generateMockSendLetter("otherUsername_invalidUser", senderId).letterCode!!
            val request = LetterVerifyRequest(letterCode)
            // when
            val response =
                mockMvc.put("/api/v1/letters/verify") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                    header("Authorization", "Bearer $accessToken")
                }
            // then
            response.andExpect {
                status { isForbidden() }
                jsonPath("$.code") {
                    value("LETTER-002")
                }
            }
        }

        @Test
        @DisplayName("다른 사용자가 이미 연람함 편지면 열람 불가")
        fun verifyLetter_With_ExpiredLetter() {
            // given
            val senderId = userMockManager.settingUser(username = "senderUsername")
            val userId = userMockManager.settingUser(username = "username")
            val otherUserId = userMockManager.settingUser(username = "otherUser")
            val accessToken = jwtMockManager.generateAccessToken(userId)
            val letterCode =
                letterMockManager.generateMockReadLetter("username", otherUserId, senderId)["letterCode"] as String
            val request = LetterVerifyRequest(letterCode)
            // when
            val response =
                mockMvc.put("/api/v1/letters/verify") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                    header("Authorization", "Bearer $accessToken")
                }
            // then
            response.andExpect {
                status { isForbidden() }
                jsonPath("$.code") {
                    value("LETTER-002")
                }
            }
        }

        @Test
        @DisplayName("이전에 열람한 적이 있다면 다시 열람 가능")
        fun verifyLetter_With_ExpiredLetter_ReAccessible() {
            // given
            val userId = userMockManager.settingUser(username = "username")
            val sender = userMockManager.settingUser(username = "senderUsername")
            val accessToken = jwtMockManager.generateAccessToken(userId)
            val letterCode =
                letterMockManager.generateMockReadLetter("username", userId, senderId = sender)["letterCode"] as String
            val request = LetterVerifyRequest(letterCode)
            // when
            val response =
                mockMvc.put("/api/v1/letters/verify") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                    header("Authorization", "Bearer $accessToken")
                }
            // then
            response.andExpect {
                status { isOk() }
                jsonPath("$.letterId") {
                    exists()
                    isString()
                    isNotEmpty()
                }
            }
        }
    }

    @Test
    fun sendLetter() {
        // given
        val request =
            SendLetterRequest(
                receiverName = "receiverName",
                content = "content",
                images = listOf("images"),
                templateType = 1,
                draftId = null,
            )
        val userId = userMockManager.settingUser()
        val accessToken = jwtMockManager.generateAccessToken(userId)
        // when
        val response =
            mockMvc.post("/api/v1/letters/send") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
                header("Authorization", "Bearer $accessToken")
            }
        // then
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
    fun sendLetter_With_DraftId() {
        // given
        val userId = userMockManager.settingUser()
        val accessToken = jwtMockManager.generateAccessToken(userId)
        val draftId = letterMockManager.generateMockDraftLetter(userId)

        val request =
            SendLetterRequest(
                receiverName = "receiverName",
                content = "content",
                images = listOf("images"),
                templateType = 1,
                draftId = draftId,
            )
        // when
        mockMvc.post("/api/v1/letters/send") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
            header("Authorization", "Bearer $accessToken")
        }
        // then
        mockMvc
            .get("/api/v1/letters/drafts/$draftId") {
                header("Authorization", "Bearer $accessToken")
            }.andExpect {
                status { isNotFound() }
            }
    }

    @Nested
    inner class GetVerifiedLetter {
        @Test
        @DisplayName("이전에 검증 완료한 편지 열람 성공")
        fun getVerifiedLetter() {
            // given
            val userId = userMockManager.settingUser(username = "username")
            val senderId = userMockManager.settingUser(username = "senderUsername")
            val accessToken = jwtMockManager.generateAccessToken(userId)
            val letterId =
                letterMockManager.generateMockReadLetter("username", userId, senderId)["letterId"] as String
            // when
            val response =
                mockMvc.get("/api/v1/letters/$letterId/verify") {
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

        @Test
        @DisplayName("이전에 검증 완료한 편지가 없음")
        fun getVerifiedLetter_With_InvalidLetterId() {
            // given
            val userId = userMockManager.settingUser(username = "username")
            val accessToken = jwtMockManager.generateAccessToken(userId)
            val letterId = "invalidLetterId"
            // when
            val response =
                mockMvc.get("/api/v1/letters/$letterId/verify") {
                    contentType = MediaType.APPLICATION_JSON
                    header("Authorization", "Bearer $accessToken")
                }
            // then
            response.andExpect {
                status { isNotFound() }
                jsonPath("$.code") {
                    value("LETTER-001")
                }
            }
        }
    }

    @Nested
    inner class AddVerifiedLetter {
        @Test
        @DisplayName("편지 열람 완료 처리 성공")
        fun addVerifiedLetter() {
            // given
            val userId = userMockManager.settingUser(username = "username")
            val senderId = userMockManager.settingUser(username = "senderUsername")
            val accessToken = jwtMockManager.generateAccessToken(userId)
            val letterId =
                letterMockManager.generateMockReadLetter(
                    receiverName = "username",
                    receiverId = userId,
                    senderId = senderId,
                )["letterId"] as String
            val request = AddVerifiedLetterRequest(letterId)
            // when
            val response =
                mockMvc.post("/api/v1/letters/verify/receive") {
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
        @DisplayName("편지 열람 완료 처리 실패")
        fun addVerifiedLetter_With_InvalidLetterId() {
            // given
            val userId = userMockManager.settingUser(username = "username")
            val accessToken = jwtMockManager.generateAccessToken(userId)
            val letterId = "invalidLetterId"
            val request = AddVerifiedLetterRequest(letterId)
            // when
            val response =
                mockMvc.post("/api/v1/letters/verify/receive") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                    header("Authorization", "Bearer $accessToken")
                }
            // then
            response.andExpect {
                status { isNotFound() }
                jsonPath("$.code") {
                    value("LETTER-001")
                }
            }
        }
    }

    @Nested
    inner class GetIndependentLetters {
        @Test
        fun getIndependentLetters() {
            // given
            val receiverId = userMockManager.settingUser()
            val senderId = userMockManager.settingUser(username = "senderUsername")
            val accessToken = jwtMockManager.generateAccessToken(receiverId)
            val independentLetter =
                letterMockManager.generateMockIndependentLetter(
                    senderId = senderId,
                    receiverId = receiverId,
                    senderName = "senderUsername",
                )
            val letterId = independentLetter.id.value
            // when
            val result =
                mockMvc.get("/api/v1/letters/independent") {
                    contentType = MediaType.APPLICATION_JSON
                    header("Authorization", "Bearer $accessToken")
                }
            // then
            result.andExpect {
                status { isOk() }
                jsonPath("$.content") {
                    exists()
                    isArray()
                    jsonPath("$.content[0].letterId") {
                        exists()
                        isString()
                        isNotEmpty()
                        value(letterId)
                    }
                    jsonPath("$.content[0].senderName") {
                        exists()
                        isString()
                        isNotEmpty()
                        value("senderUsername")
                    }
                    jsonPath("$.content[0].isNew") {
                        exists()
                        isBoolean()
                        value(true)
                    }
                }
            }
        }

        @Test
        fun getIndependentLetters_get_movedAt_sorted() {
            // given
            val receiverId = userMockManager.settingUser()
            val senderId = userMockManager.settingUser(username = "senderUsername")
            val space = spaceMockManager.settingSpace(receiverId)
            val accessToken = jwtMockManager.generateAccessToken(receiverId)

            val spaceLetter =
                letterMockManager.generateMockSpaceLetter(
                    senderId = senderId,
                    receiverId = receiverId,
                    senderName = "senderUsername",
                    spaceId = space.id.value,
                )

            val independentLetters =
                (0..1).map { _ ->
                    letterMockManager.generateMockIndependentLetter(
                        senderId = senderId,
                        receiverId = receiverId,
                        senderName = "senderUsername",
                    )
                }

            mockMvc.put("/api/v1/spaces/letters/${spaceLetter.id.value}/independent") {
                contentType = MediaType.APPLICATION_JSON
                header("Authorization", "Bearer $accessToken")
            }

            // when
            val result =
                mockMvc.get("/api/v1/letters/independent") {
                    contentType = MediaType.APPLICATION_JSON
                    header("Authorization", "Bearer $accessToken")
                }

            // then
            result
                .andExpect {
                    status { isOk() }
                    jsonPath("$.content") {
                        exists()
                        isArray()
                        jsonPath("$.content[0].letterId") {
                            exists()
                            isString()
                            isNotEmpty()
                            value(spaceLetter.id.value)
                        }
                        jsonPath("$.content[1].letterId") {
                            exists()
                            isString()
                            isNotEmpty()
                            value(independentLetters[1].id.value)
                        }
                        jsonPath("$.content[2].letterId") {
                            exists()
                            isString()
                            isNotEmpty()
                            value(independentLetters[0].id.value)
                        }
                    }
                }.andDo { print() }
        }
    }

    @Test
    fun getIndependentLetter_IsNewFalse_IsAfter() {
        // given
        val receiverId = userMockManager.settingUser()
        val senderId = userMockManager.settingUser(username = "senderUsername")
        val accessToken = jwtMockManager.generateAccessToken(receiverId)
        val independentLetter =
            letterMockManager.generateMockIndependentLetter(
                senderId = senderId,
                receiverId = receiverId,
                senderName = "senderUsername",
                movedAt = LocalDateTime.now().minusDays(2),
            )
        val letterId = independentLetter.id.value
        // when
        val result =
            mockMvc.get("/api/v1/letters/independent") {
                contentType = MediaType.APPLICATION_JSON
                header("Authorization", "Bearer $accessToken")
            }
        // then
        result.andExpect {
            status { isOk() }
            jsonPath("$.content") {
                exists()
                isArray()
                jsonPath("$.content[0].letterId") {
                    exists()
                    isString()
                    isNotEmpty()
                    value(letterId)
                }
                jsonPath("$.content[0].senderName") {
                    exists()
                    isString()
                    isNotEmpty()
                    value("senderUsername")
                }
                jsonPath("$.content[0].isNew") {
                    exists()
                    isBoolean()
                    value(false)
                }
            }
        }
    }

    @Test
    fun getIndependentLetter_IsNewFalse_IsBefore_Opened() {
        // given
        val receiverId = userMockManager.settingUser()
        val senderId = userMockManager.settingUser(username = "senderUsername")
        val accessToken = jwtMockManager.generateAccessToken(receiverId)
        val independentLetter =
            letterMockManager.generateMockIndependentLetter(
                senderId = senderId,
                receiverId = receiverId,
                senderName = "senderUsername",
                isOpened = true,
            )
        val letterId = independentLetter.id.value
        // when
        val result =
            mockMvc.get("/api/v1/letters/independent") {
                contentType = MediaType.APPLICATION_JSON
                header("Authorization", "Bearer $accessToken")
            }
        // then
        result.andExpect {
            status { isOk() }
            jsonPath("$.content") {
                exists()
                isArray()
                jsonPath("$.content[0].letterId") {
                    exists()
                    isString()
                    isNotEmpty()
                    value(letterId)
                }
                jsonPath("$.content[0].senderName") {
                    exists()
                    isString()
                    isNotEmpty()
                    value("senderUsername")
                }
                jsonPath("$.content[0].isNew") {
                    exists()
                    isBoolean()
                    value(false)
                }
            }
        }
    }

    @Test
    fun getIndependentLetterDetail() {
        // given
        val receiverId = userMockManager.settingUser()
        val senderId = userMockManager.settingUser(username = "senderUsername")
        val accessToken = jwtMockManager.generateAccessToken(receiverId)
        val independentLetter =
            letterMockManager.generateMockIndependentLetter(
                senderId = senderId,
                receiverId = receiverId,
                senderName = "senderUsername",
            )
        val letterId = independentLetter.id.value
        // when
        val result =
            mockMvc.get("/api/v1/letters/independent/$letterId") {
                contentType = MediaType.APPLICATION_JSON
                header("Authorization", "Bearer $accessToken")
            }
        // then
        result.andExpect {
            status { isOk() }
            jsonPath("$.senderName") {
                exists()
                isString()
                isNotEmpty()
                value("senderUsername")
            }
            jsonPath("$.letterCount") {
                exists()
                isNumber()
                value(1)
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
                doesNotExist()
            }
            jsonPath("$.nextLetter") {
                doesNotExist()
            }
        }
    }

    @Test
    fun addPhysicalLetter() {
        // given
        val request =
            AddPhysicalLetterRequest(
                senderName = "senderName",
                content = "content",
                images = listOf("images"),
                templateType = 1,
            )
        val userId = userMockManager.settingUser()
        val accessToken = jwtMockManager.generateAccessToken(userId)
        // when
        val response =
            mockMvc.post("/api/v1/letters/physical/receive") {
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
    fun deleteIndependentLetter() {
        // given
        val receiverId = userMockManager.settingUser()
        val senderId = userMockManager.settingUser(username = "senderUsername")
        val accessToken = jwtMockManager.generateAccessToken(receiverId)
        val independentLetter =
            letterMockManager.generateMockIndependentLetter(
                senderId = senderId,
                receiverId = receiverId,
                senderName = "senderUsername",
            )
        val letterId = independentLetter.id.value
        // when
        mockMvc.delete("/api/v1/letters/independent/$letterId") {
            contentType = MediaType.APPLICATION_JSON
            header("Authorization", "Bearer $accessToken")
        }

        val response =
            mockMvc.get("/api/v1/letters/independent/$letterId") {
                contentType = MediaType.APPLICATION_JSON
                header("Authorization", "Bearer $accessToken")
            }

        // then
        response.andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun updateIndependentLetter() {
        // given
        val receiverId = userMockManager.settingUser()
        val senderId = userMockManager.settingUser(username = "senderUsername")
        val accessToken = jwtMockManager.generateAccessToken(receiverId)
        val independentLetter =
            letterMockManager.generateMockIndependentLetter(
                senderId = senderId,
                receiverId = receiverId,
                senderName = "senderUsername",
            )
        val letterId = independentLetter.id.value
        val request =
            ModifyLetterRequest(
                content = "content",
                images = listOf("images"),
                senderName = "senderName",
                templateType = 1,
            )
        // when
        val response =
            mockMvc.put("/api/v1/letters/independent/$letterId/content") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
                header("Authorization", "Bearer $accessToken")
            }
        // then
        response.andExpect {
            status { isOk() }
        }
    }

    @Nested
    inner class GetAllLetterCount {
        @Test
        fun getAllLetterCount() {
            // given
            val senderId = userMockManager.settingUser()
            val receiverId = userMockManager.settingUser()
            val accessToken = jwtMockManager.generateAccessToken(receiverId)
            (0..3).forEach {
                letterMockManager.generateMockIndependentLetter(
                    senderId = senderId,
                    receiverId = receiverId,
                    senderName = "senderUsername",
                )
            }
            // when
            val response =
                mockMvc.get("/api/v1/letters/count") {
                    contentType = MediaType.APPLICATION_JSON
                    header("Authorization", "Bearer $accessToken")
                }
            // then
            response.andExpect {
                status { isOk() }
                jsonPath("$.letterCount") {
                    exists()
                    isNumber()
                    value(4)
                }
            }
        }

        @Test
        fun getAllLetterCount_with_letter_delete_independent() {
            // given
            val senderId = userMockManager.settingUser()
            val receiverId = userMockManager.settingUser()
            val accessToken = jwtMockManager.generateAccessToken(receiverId)
            val letters =
                (0..3).map {
                    letterMockManager.generateMockIndependentLetter(
                        senderId = senderId,
                        receiverId = receiverId,
                        senderName = "senderUsername",
                    )
                }

            mockMvc.delete("/api/v1/letters/independent/${letters[0].id.value}") {
                contentType = MediaType.APPLICATION_JSON
                header("Authorization", "Bearer $accessToken")
            }

            // when
            val response =
                mockMvc.get("/api/v1/letters/count") {
                    contentType = MediaType.APPLICATION_JSON
                    header("Authorization", "Bearer $accessToken")
                }
            // then
            response.andExpect {
                status { isOk() }
                jsonPath("$.letterCount") {
                    exists()
                    isNumber()
                    value(3)
                }
            }
        }

        @Test
        fun getAllLetterCount_with_letter_delete_space() {
            // given
            val senderId = userMockManager.settingUser()
            val receiverId = userMockManager.settingUser()
            val accessToken = jwtMockManager.generateAccessToken(receiverId)
            val space = spaceMockManager.settingSpace(receiverId)
            val letters =
                (0..3).map {
                    letterMockManager.generateMockSpaceLetter(
                        senderId = senderId,
                        receiverId = receiverId,
                        senderName = "senderUsername",
                        spaceId = space.id.value,
                    )
                }

            mockMvc.delete("/api/v1/spaces/letters/${letters[0].id.value}") {
                contentType = MediaType.APPLICATION_JSON
                header("Authorization", "Bearer $accessToken")
            }

            // when
            val response =
                mockMvc.get("/api/v1/letters/count") {
                    contentType = MediaType.APPLICATION_JSON
                    header("Authorization", "Bearer $accessToken")
                }
            // then
            response.andExpect {
                status { isOk() }
                jsonPath("$.letterCount") {
                    exists()
                    isNumber()
                    value(3)
                }
            }
        }

        @Test
        fun getAllLetterCount_with_letter_delete_send() {
            // given
            val senderId = userMockManager.settingUser()
            val receiverId = userMockManager.settingUser()
            val accessToken = jwtMockManager.generateAccessToken(receiverId)
            val space = spaceMockManager.settingSpace(receiverId)
            val spaceLetters =
                (0..3).map {
                    letterMockManager.generateMockSpaceLetter(
                        senderId = senderId,
                        receiverId = receiverId,
                        senderName = "senderUsername",
                        spaceId = space.id.value,
                    )
                }
            (0..3).forEach { _ ->
                letterMockManager.generateMockIndependentLetter(
                    senderId = senderId,
                    receiverId = receiverId,
                    senderName = "senderUsername",
                )
            }

            mockMvc.delete("/api/v1/spaces/letters/${spaceLetters[0].id.value}") {
                contentType = MediaType.APPLICATION_JSON
                header("Authorization", "Bearer $accessToken")
            }

            // when
            val response =
                mockMvc.get("/api/v1/letters/count") {
                    contentType = MediaType.APPLICATION_JSON
                    header("Authorization", "Bearer $accessToken")
                }
            // then
            response.andExpect {
                status { isOk() }
                jsonPath("$.letterCount") {
                    exists()
                    isNumber()
                    value(7)
                }
                jsonPath("$.spaceCount") {
                    exists()
                    isNumber()
                    value(1)
                }
            }
        }
    }

    @Test
    fun getAllSendLetterHistory() {
        // given
        val senderId = userMockManager.settingUser()
        val accessToken = jwtMockManager.generateAccessToken(senderId)
        val sendLetters =
            (0..3).map {
                letterMockManager.generateMockSendLetter(
                    receiverName = "receiverName",
                    senderId = senderId,
                )
            }
        // when
        val response =
            mockMvc.get("/api/v1/letters/send") {
                contentType = MediaType.APPLICATION_JSON
                header("Authorization", "Bearer $accessToken")
            }
        // then
        response.andExpect {
            status { isOk() }
            jsonPath("$.content") {
                exists()
                isArray()
                (0..3).forEach {
                    jsonPath("$.content[$it].letterId") {
                        exists()
                        isString()
                        isNotEmpty()
                        value(sendLetters[it].id.value)
                    }
                    jsonPath("$.content[$it].receiverName") {
                        exists()
                        isString()
                        isNotEmpty()
                        value(sendLetters[it].receiverName)
                    }
                    jsonPath("$.content[$it].sendDate") {
                        exists()
                        isString()
                        isNotEmpty()
                        value(sendLetters[it].createdDate.toString())
                    }
                }
            }
        }
    }

    @Test
    fun getSendLetterDetail() {
        // given
        val senderId = userMockManager.settingUser()
        val accessToken = jwtMockManager.generateAccessToken(senderId)
        val sendLetter =
            letterMockManager.generateMockSendLetter(
                receiverName = "receiverName",
                senderId = senderId,
            )
        val letterId = sendLetter.id.value
        // when
        val response =
            mockMvc.get("/api/v1/letters/send/$letterId") {
                contentType = MediaType.APPLICATION_JSON
                header("Authorization", "Bearer $accessToken")
            }
        // then
        response.andExpect {
            status { isOk() }
            jsonPath("$.receiverName") {
                exists()
                isString()
                isNotEmpty()
                value("receiverName")
            }
            jsonPath("$.sendDate") {
                exists()
                isString()
                isNotEmpty()
            }
            jsonPath("$.content") {
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
        }
    }

    @Nested
    inner class DeleteSendLetter {
        @Test
        fun deleteSendLetter() {
            // given
            val senderId = userMockManager.settingUser()
            val accessToken = jwtMockManager.generateAccessToken(senderId)
            val sendLetter =
                letterMockManager.generateMockSendLetter(
                    receiverName = "receiverName",
                    senderId = senderId,
                )
            val letterId = sendLetter.id.value
            // when
            mockMvc.delete("/api/v1/letters/send/$letterId") {
                contentType = MediaType.APPLICATION_JSON
                header("Authorization", "Bearer $accessToken")
            }
            val response =
                mockMvc.get("/api/v1/letters/send/$letterId") {
                    contentType = MediaType.APPLICATION_JSON
                    header("Authorization", "Bearer $accessToken")
                }
            // then
            response.andExpect {
                status { isNotFound() }
            }
        }

        @Test
        fun deleteSendLetters() {
            // given
            val senderId = userMockManager.settingUser()
            val accessToken = jwtMockManager.generateAccessToken(senderId)
            val sendLetters =
                (0..3).map {
                    letterMockManager.generateMockSendLetter(
                        receiverName = "receiverName",
                        senderId = senderId,
                    )
                }
            val request = DeleteSendLettersRequest(sendLetters.map { it.id.value })
            // when
            mockMvc.delete("/api/v1/letters/send") {
                contentType = MediaType.APPLICATION_JSON
                header("Authorization", "Bearer $accessToken")
                content = objectMapper.writeValueAsString(request)
            }
            val response =
                mockMvc.get("/api/v1/letters/send") {
                    contentType = MediaType.APPLICATION_JSON
                    header("Authorization", "Bearer $accessToken")
                }
            // then
            response.andExpect {
                status { isOk() }
                jsonPath("$.content") {
                    exists()
                    isArray()
                    isEmpty()
                }
            }
        }
    }
}
