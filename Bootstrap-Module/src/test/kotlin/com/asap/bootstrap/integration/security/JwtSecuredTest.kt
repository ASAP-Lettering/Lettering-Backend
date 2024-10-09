package com.asap.bootstrap.integration.security

import com.asap.bootstrap.IntegrationSupporter
import com.asap.security.jwt.user.TokenType
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.get

class JwtSecuredTest : IntegrationSupporter() {
    @Test
    fun `should return 401 when no token is provided`() {
        // Arrange
        // Act Assert
        mockMvc
            .get("/security") {
            }.andExpect {
                status { is5xxServerError() }
                jsonPath("$.code") { value("UNKNOWN-ERROR") }
            }
    }

    @Test
    fun `should return 401 when invalid token is provided`() {
        // Arrange
        val invalidToken = "invalid_token"
        // Act Assert
        mockMvc
            .get("/security") {
                header("Authorization", "Bearer $invalidToken")
            }.andExpect {
                status { isUnauthorized() }
                jsonPath("$.message") { value("유효하지 않은 토큰입니다.") }
                jsonPath("$.code") { value("TOKEN-001") }
            }.andDo { print() } // Assert
    }

    @Test
    fun `should return 401 when expired token is provided`() {
        // Arrange
        val expiredToken = testJwtDataGenerator.generateExpiredToken(TokenType.ACCESS)
        // Act Assert
        mockMvc
            .get("/security") {
                header("Authorization", "Bearer $expiredToken")
            }.andExpect {
                status { isUnauthorized() }
                jsonPath("$.message") { value("만료된 토큰입니다.") }
                jsonPath("$.code") { value("TOKEN-002") }
            }.andDo { print() }
    }

    @Test
    fun `should return 401 when expired token is provided with AccessUser`() {
        // Arrange
        val expiredToken = testJwtDataGenerator.generateExpiredToken(TokenType.ACCESS)
        // Act Assert
        mockMvc
            .get("/security/secured") {
                header("Authorization", "Bearer $expiredToken")
            }.andExpect {
                status { isUnauthorized() }
                jsonPath("$.message") { value("만료된 토큰입니다.") }
                jsonPath("$.code") { value("TOKEN-002") }
            }.andDo { print() }
    }
}
