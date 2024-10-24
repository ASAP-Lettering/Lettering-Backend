package com.asap.bootstrap.acceptance.letter.controller

import com.asap.application.letter.port.`in`.*
import com.asap.bootstrap.acceptance.letter.LetterAcceptanceSupporter
import com.asap.bootstrap.letter.dto.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import java.time.LocalDate

class LetterControllerTest : LetterAcceptanceSupporter() {
    @Test
    fun verifyLetter() {
        // given
        val userId = userMockManager.settingUser()
        val accessToken = jwtMockManager.generateAccessToken(userId)

        val request = LetterVerifyRequest("letterCode")
        BDDMockito
            .given(
                verifyLetterAccessibleUsecase.verify(
                    VerifyLetterAccessibleUsecase.Command(
                        letterCode = request.letterCode,
                        userId = userId,
                    ),
                ),
            ).willReturn(VerifyLetterAccessibleUsecase.Response("letterId"))
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
    fun sendLetter() {
        // given
        val request =
            SendLetterRequest(
                receiverName = "receiverName",
                content = "content",
                images = listOf("images"),
                templateType = 1,
                draftId = "draftId",
            )
        val userId = userMockManager.settingUser()
        val accessToken = jwtMockManager.generateAccessToken(userId)
        BDDMockito
            .given(
                sendLetterUsecase.send(
                    SendLetterUsecase.Command(
                        userId = userId,
                        receiverName = request.receiverName,
                        content = request.content,
                        images = request.images,
                        templateType = request.templateType,
                        draftId = request.draftId,
                    ),
                ),
            ).willReturn(SendLetterUsecase.Response("letterCode"))
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
    fun getVerifiedLetter() {
        // given
        val userId = userMockManager.settingUser()
        val accessToken = jwtMockManager.generateAccessToken(userId)

        val verifiedLetterInfoResponse =
            GetVerifiedLetterUsecase.Response(
                senderName = "sendName",
                content = "content",
                sendDate = LocalDate.now(),
                templateType = 1,
                images = listOf("images"),
            )
        BDDMockito
            .given(
                getVerifiedLetterUsecase.get(
                    GetVerifiedLetterUsecase.Query(
                        letterId = "letterId",
                        userId = userId,
                    ),
                ),
            ).willReturn(verifiedLetterInfoResponse)
        // when
        val response =
            mockMvc.get("/api/v1/letters/{letterId}/verify", "letterId") {
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
    fun addReceiveLetter() {
        // given
        val userId = userMockManager.settingUser()
        val accessToken = jwtMockManager.generateAccessToken(userId)

        val request =
            AddVerifiedLetterRequest(
                letterId = "letterId",
            )
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
    fun getIndependentLetters() {
        // given
        val userId = userMockManager.settingUser()
        val accessToken = jwtMockManager.generateAccessToken(userId)

        val letterInfo =
            GetIndependentLettersUsecase.LetterInfo(
                letterId = "letterId",
                senderName = "senderName",
                isNew = true,
            )
        val response =
            GetIndependentLettersUsecase.Response.All(
                letters = listOf(letterInfo),
            )
        BDDMockito
            .given(
                getIndependentLettersUsecase.getAll(
                    GetIndependentLettersUsecase.QueryAll(
                        userId = userId,
                    ),
                ),
            ).willReturn(response)
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
                }
                jsonPath("$.content[0].senderName") {
                    exists()
                    isString()
                    isNotEmpty()
                }
                jsonPath("$.content[0].isNew") {
                    exists()
                    isBoolean()
                }
            }
        }
    }

    @Test
    fun getIndependentLetterDetail() {
        // given
        val userId = userMockManager.settingUser()
        val accessToken = jwtMockManager.generateAccessToken(userId)

        val response =
            GetIndependentLettersUsecase.Response.One(
                senderName = "senderName",
                letterCount = 1,
                content = "content",
                sendDate = LocalDate.now(),
                images = listOf("images"),
                templateType = 1,
                prevLetter =
                    GetIndependentLettersUsecase.NearbyLetter(
                        letterId = "prevLetterId",
                        senderName = "prevSenderName",
                    ),
                nextLetter =
                    GetIndependentLettersUsecase.NearbyLetter(
                        letterId = "nextLetterId",
                        senderName = "nextSenderName",
                    ),
            )
        BDDMockito
            .given(
                getIndependentLettersUsecase.get(
                    GetIndependentLettersUsecase.Query(
                        userId = userId,
                        letterId = "letterId",
                    ),
                ),
            ).willReturn(response)
        // when
        val result =
            mockMvc.get("/api/v1/letters/independent/{letterId}", "letterId") {
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
                    isNotEmpty()
                }
                jsonPath("$.prevLetter.senderName") {
                    exists()
                    isString()
                    isNotEmpty()
                }
            }
            jsonPath("$.nextLetter") {
                exists()
                jsonPath("$.nextLetter.letterId") {
                    exists()
                    isString()
                    isNotEmpty()
                }
                jsonPath("$.nextLetter.senderName") {
                    exists()
                    isString()
                    isNotEmpty()
                }
            }
        }
    }

    @Test
    fun addPhysicalLetter() {
        // given
        val userId = userMockManager.settingUser()
        val accessToken = jwtMockManager.generateAccessToken(userId)

        val request =
            AddPhysicalLetterRequest(
                senderName = "senderName",
                content = "content",
                images = listOf("images"),
                templateType = 1,
            )
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
        val userId = userMockManager.settingUser()
        val accessToken = jwtMockManager.generateAccessToken(userId)

        // when
        val response =
            mockMvc.delete("/api/v1/letters/independent/{letterId}", "letterId") {
                contentType = MediaType.APPLICATION_JSON
                header("Authorization", "Bearer $accessToken")
            }
        // then
        response.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun updateIndependentLetter() {
        // given
        val userId = userMockManager.settingUser()
        val accessToken = jwtMockManager.generateAccessToken(userId)

        val request =
            ModifyLetterRequest(
                senderName = "senderName",
                content = "content",
                images = listOf("images"),
                templateType = 1,
            )
        // when
        val response =
            mockMvc.put("/api/v1/letters/independent/{letterId}/content", "letterId") {
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
    fun getAllLetterCount() {
        // given
        val userId = userMockManager.settingUser()
        val accessToken = jwtMockManager.generateAccessToken(userId)

        val response = GetAllLetterCountUsecase.Response(5, 5)
        BDDMockito
            .given(
                getAllLetterCountUsecase.get(
                    GetAllLetterCountUsecase.Query(
                        userId = userId,
                    ),
                ),
            ).willReturn(response)
        // when
        val result =
            mockMvc.get("/api/v1/letters/count") {
                contentType = MediaType.APPLICATION_JSON
                header("Authorization", "Bearer $accessToken")
            }
        // then
        result.andExpect {
            status { isOk() }
            jsonPath("$.letterCount") {
                exists()
                isNumber()
                value(5)
            }
            jsonPath("$.spaceCount") {
                exists()
                isNumber()
                value(5)
            }
        }
    }

    @Test
    fun getAllSendLetterHistory() {
        // given
        val userId = userMockManager.settingUser()
        val accessToken = jwtMockManager.generateAccessToken(userId)
        val response =
            (0..2).map {
                GetSendLetterUsecase.Response.History(
                    letterId = "letterId$it",
                    receiverName = "receiverName$it",
                    sendDate = LocalDate.now(),
                )
            }
        BDDMockito
            .given(
                getSendLetterUsecase.getHistory(
                    GetSendLetterUsecase.Query.AllHistory(
                        userId = userId,
                    ),
                ),
            ).willReturn(response)

        // when
        val result =
            mockMvc.get("/api/v1/letters/send") {
                contentType = MediaType.APPLICATION_JSON
                header("Authorization", "Bearer $accessToken")
            }
        // then
        result.andExpect {
            status { isOk() }
            jsonPath("$.content") {
                exists()
                isArray()
                (0..2).forEach {
                    jsonPath("$.content[$it].letterId") {
                        exists()
                        isString()
                        isNotEmpty()
                    }
                    jsonPath("$.content[$it].receiverName") {
                        exists()
                        isString()
                        isNotEmpty()
                    }
                    jsonPath("$.content[$it].sendDate") {
                        exists()
                        isString()
                        isNotEmpty()
                    }
                }
            }
        }
    }

    @Test
    fun getSendLetterDetail() {
        // given
        val userId = userMockManager.settingUser()
        val accessToken = jwtMockManager.generateAccessToken(userId)
        val response =
            GetSendLetterUsecase.Response.Detail(
                receiverName = "receiverName",
                sendDate = LocalDate.now(),
                content = "content",
                images = listOf("images"),
                templateType = 1,
                letterCode = "letterCode",
            )
        BDDMockito
            .given(
                getSendLetterUsecase.getDetail(
                    GetSendLetterUsecase.Query.Detail(
                        userId = userId,
                        letterId = "letterId",
                    ),
                ),
            ).willReturn(response)
        // when
        val result =
            mockMvc.get("/api/v1/letters/send/{letterId}", "letterId") {
                contentType = MediaType.APPLICATION_JSON
                header("Authorization", "Bearer $accessToken")
            }
        // then
        result.andExpect {
            status { isOk() }
            jsonPath("$.receiverName") {
                exists()
                isString()
                isNotEmpty()
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
            jsonPath("$.letterCode") {
                exists()
                isString()
            }
        }
    }

    @Nested
    inner class DeleteSendLetter {
        @Test
        fun deleteSendLetter() {
            // given
            val userId = userMockManager.settingUser()
            val accessToken = jwtMockManager.generateAccessToken(userId)
            // when
            val response =
                mockMvc.delete("/api/v1/letters/send/{letterId}", "letterId") {
                    contentType = MediaType.APPLICATION_JSON
                    header("Authorization", "Bearer $accessToken")
                }
            // then
            response.andExpect {
                status { isOk() }
            }
        }

        @Test
        fun deleteSendLetters() {
            // given
            val userId = userMockManager.settingUser()
            val accessToken = jwtMockManager.generateAccessToken(userId)
            val request = DeleteSendLettersRequest(listOf("letterId1", "letterId2"))
            // when
            val response =
                mockMvc.delete("/api/v1/letters/send") {
                    contentType = MediaType.APPLICATION_JSON
                    header("Authorization", "Bearer $accessToken")
                    content = objectMapper.writeValueAsString(request)
                }
            // then
            response.andExpect {
                status { isOk() }
            }
        }
    }
}
