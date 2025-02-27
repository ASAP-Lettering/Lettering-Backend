package com.asap.bootstrap.integration.letter

import com.asap.application.letter.LetterMockManager
import com.asap.application.letter.exception.LetterException
import com.asap.application.letter.port.out.ReceiveDraftLetterManagementPort
import com.asap.bootstrap.IntegrationSupporter
import com.asap.bootstrap.web.letter.dto.UpdateDraftLetterRequest
import com.asap.bootstrap.web.letter.dto.UpdatePhysicalDraftLetterRequest
import com.asap.domain.common.DomainId
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

class DraftLetterApiIntegrationTest(
    private val receiveDraftLetterManagementPort: ReceiveDraftLetterManagementPort,
    private val letterMockManager: LetterMockManager,
) : IntegrationSupporter() {

    @Test
    fun `get draft key`() {
        // given
        val userId = userMockManager.settingUser()
        val accessToken = jwtMockManager.generateAccessToken(userId)
        // when
        val response =
            mockMvc.post("/api/v1/letters/drafts/key") {
                header("Authorization", "Bearer $accessToken")
            }

        // then
        response.andExpect {
            status { isOk() }
            jsonPath("$.draftId") { isString() }
        }
    }

    @Test
    fun `update draft`() {
        // given
        val userId = userMockManager.settingUser()
        val accessToken = jwtMockManager.generateAccessToken(userId)
        val draftId = letterMockManager.generateMockDraftLetter(userId)
        val request =
            UpdateDraftLetterRequest(
                content = "content",
                receiverName = "receiverName",
                images = listOf("image"),
            )
        // when
        val response =
            mockMvc.post("/api/v1/letters/drafts/$draftId") {
                contentType = MediaType.APPLICATION_JSON
                header("Authorization", "Bearer $accessToken")
                content = objectMapper.writeValueAsString(request)
            }

        // then
        response.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `get all drafts`() {
        // given
        val userId = userMockManager.settingUser()
        val accessToken = jwtMockManager.generateAccessToken(userId)
        letterMockManager.generateMockDraftLetter(userId)
        // when
        val response =
            mockMvc.get("/api/v1/letters/drafts") {
                header("Authorization", "Bearer $accessToken")
            }

        // then
        response.andExpect {
            status { isOk() }
            jsonPath("$.drafts[0].draftKey") { isString() }
            jsonPath("$.drafts[0].receiverName") { isString() }
            jsonPath("$.drafts[0].content") { isString() }
            jsonPath("$.drafts[0].lastUpdated") { isString() }
        }
    }

    @Test
    fun `get draft letter`() {
        // given
        val userId = userMockManager.settingUser()
        val accessToken = jwtMockManager.generateAccessToken(userId)
        val draftId = letterMockManager.generateMockDraftLetter(userId)
        // when
        val response =
            mockMvc.get("/api/v1/letters/drafts/$draftId") {
                header("Authorization", "Bearer $accessToken")
            }

        // then
        response.andExpect {
            status { isOk() }
            jsonPath("$.draftKey") { isString() }
            jsonPath("$.receiverName") { isString() }
            jsonPath("$.content") { isString() }
            jsonPath("$.images") { isArray() }
        }
    }

    @Test
    fun `get draft count`() {
        // given
        val userId = userMockManager.settingUser()
        val accessToken = jwtMockManager.generateAccessToken(userId)
        letterMockManager.generateMockDraftLetter(userId)
        // when
        val response =
            mockMvc.get("/api/v1/letters/drafts/count") {
                header("Authorization", "Bearer $accessToken")
            }

        // then
        response.andExpect {
            status { isOk() }
            jsonPath("$.count") {
                isNumber()
                value(1)
            }
        }
    }

    @Test
    fun `delete draft`() {
        // given
        val userId = userMockManager.settingUser()
        val accessToken = jwtMockManager.generateAccessToken(userId)
        val draftId = letterMockManager.generateMockDraftLetter(userId)
        // when
        val response =
            mockMvc.delete("/api/v1/letters/drafts/$draftId") {
                contentType = MediaType.APPLICATION_JSON
                header("Authorization", "Bearer $accessToken")
            }

        // then
        response.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `get physical draft key`() {
        // given
        val userId = userMockManager.settingUser()
        val accessToken = jwtMockManager.generateAccessToken(userId)
        // when
        val response =
            mockMvc.post("/api/v1/letters/drafts/physical/key") {
                header("Authorization", "Bearer $accessToken")
            }

        // then
        response.andExpect {
            status { isOk() }
            jsonPath("$.draftId") { isString() }
        }
    }

    @Nested
    @DisplayName("update physical draft")
    inner class UpdatePhysicalDraft {
        @Test
        fun `update physical draft`() {
            // given
            val userId = userMockManager.settingUser()
            val accessToken = jwtMockManager.generateAccessToken(userId)
            val draftId = letterMockManager.generateMockReceiveDraftLetter(userId)
            val request =
                UpdatePhysicalDraftLetterRequest(
                    content = "content",
                    images = listOf("image"),
                    senderName = "senderName",
                )
            // when
            val response =
                mockMvc.post("/api/v1/letters/drafts/physical/$draftId") {
                    contentType = MediaType.APPLICATION_JSON
                    header("Authorization", "Bearer $accessToken")
                    content = objectMapper.writeValueAsString(request)
                }

            // then
            response.andExpect {
                status { isOk() }
            }
            receiveDraftLetterManagementPort.getDraftLetterNotNull(
                draftId = DomainId(draftId),
                ownerId = DomainId(userId)
            ).apply {
                content shouldBe "content"
                senderName shouldBe "senderName"
                images shouldBe listOf("image")
            }
        }

        @Test
        fun `throw exception when draft not found`() {
            // given
            val userId = userMockManager.settingUser()
            val accessToken = jwtMockManager.generateAccessToken(userId)
            val draftId = "notFound"
            val request =
                UpdatePhysicalDraftLetterRequest(
                    content = "content",
                    images = listOf("image"),
                    senderName = "senderName",
                )
            // when
            val response =
                mockMvc.post("/api/v1/letters/drafts/physical/$draftId") {
                    contentType = MediaType.APPLICATION_JSON
                    header("Authorization", "Bearer $accessToken")
                    content = objectMapper.writeValueAsString(request)
                }

            // then
            response.andExpect {
                status { isNotFound() }
            }
        }
    }

    @Nested
    @DisplayName("get physical drafts")
    inner class GetPhysicalDraft {

        @Test
        fun `get all physical drafts`() {
            // given
            val userId = userMockManager.settingUser()
            val accessToken = jwtMockManager.generateAccessToken(userId)
            val draftId = letterMockManager.generateMockReceiveDraftLetter(userId)

            val request = UpdatePhysicalDraftLetterRequest(
                content = "content",
                images = listOf("image"),
                senderName = "senderName",
            )

            mockMvc.post("/api/v1/letters/drafts/physical/$draftId") {
                contentType = MediaType.APPLICATION_JSON
                header("Authorization", "Bearer $accessToken")
                content = objectMapper.writeValueAsString(request)
            }

            // when
            val response =
                mockMvc.get("/api/v1/letters/drafts/physical") {
                    header("Authorization", "Bearer $accessToken")
                }

            // then
            response.andExpect {
                status { isOk() }
                jsonPath("$.drafts[0].draftKey") { value(draftId) }
                jsonPath("$.drafts[0].senderName") { value(request.senderName) }
                jsonPath("$.drafts[0].content") { value(request.content) }
                jsonPath("$.drafts[0].lastUpdated") { isString() }
            }
        }

        @Test
        fun `get physical draft letter`() {
            // given
            val userId = userMockManager.settingUser()
            val accessToken = jwtMockManager.generateAccessToken(userId)
            val draftId = letterMockManager.generateMockReceiveDraftLetter(userId)
            val request = UpdatePhysicalDraftLetterRequest(
                content = "content",
                images = listOf("image"),
                senderName = "senderName",
            )

            mockMvc.post("/api/v1/letters/drafts/physical/$draftId") {
                contentType = MediaType.APPLICATION_JSON
                header("Authorization", "Bearer $accessToken")
                content = objectMapper.writeValueAsString(request)
            }

            // when
            val response =
                mockMvc.get("/api/v1/letters/drafts/physical/$draftId") {
                    header("Authorization", "Bearer $accessToken")
                }

            // then
            response.andExpect {
                status { isOk() }
                jsonPath("$.draftKey") { value(draftId) }
                jsonPath("$.senderName") { value(request.senderName) }
                jsonPath("$.content") { value(request.content) }
                jsonPath("$.images") { isArray() }
            }
        }

        @Test
        fun `throw exception when physical draft not found`() {
            // given
            val userId = userMockManager.settingUser()
            val accessToken = jwtMockManager.generateAccessToken(userId)
            val draftId = "notFound"
            // when
            val response =
                mockMvc.get("/api/v1/letters/drafts/physical/$draftId") {
                    header("Authorization", "Bearer $accessToken")
                }

            // then
            response.andExpect {
                status { isNotFound() }
            }
        }
    }

    @Nested
    @DisplayName("delete physical draft")
    inner class DeletePhysicalDraft {
        @Test
        fun `delete physical draft`() {
            // given
            val userId = userMockManager.settingUser()
            val accessToken = jwtMockManager.generateAccessToken(userId)
            val draftId = letterMockManager.generateMockReceiveDraftLetter(userId)
            // when
            val response =
                mockMvc.delete("/api/v1/letters/drafts/physical/$draftId") {
                    contentType = MediaType.APPLICATION_JSON
                    header("Authorization", "Bearer $accessToken")
                }

            // then
            response.andExpect {
                status { isOk() }
            }
            shouldThrow<LetterException.DraftLetterNotFoundException> {
                receiveDraftLetterManagementPort.getDraftLetterNotNull(
                    draftId = DomainId(draftId),
                    ownerId = DomainId(userId)
                )
            }
        }

        @Test
        fun `throw exception when physical draft not found`() {
            // given
            val userId = userMockManager.settingUser()
            val accessToken = jwtMockManager.generateAccessToken(userId)
            val draftId = "notFound"
            // when
            val response =
                mockMvc.delete("/api/v1/letters/drafts/physical/$draftId") {
                    contentType = MediaType.APPLICATION_JSON
                    header("Authorization", "Bearer $accessToken")
                }

            // then
            response.andExpect {
                status { isNotFound() }
            }
        }
    }

    @Nested
    @DisplayName("get physical draft count")
    inner class GetPhysicalDraftCount {
        @Test
        fun `get physical draft count`() {
            // given
            val userId = userMockManager.settingUser()
            val accessToken = jwtMockManager.generateAccessToken(userId)

            val draftLetterCount = 10

            (1..draftLetterCount).forEach {
                letterMockManager.generateMockReceiveDraftLetter(userId)
            }

            // when
            val response =
                mockMvc.get("/api/v1/letters/drafts/physical/count") {
                    header("Authorization", "Bearer $accessToken")
                }

            // then
            response.andExpect {
                status { isOk() }
                jsonPath("$.count") {
                    isNumber()
                    value(10)
                }
            }
        }
    }

}
