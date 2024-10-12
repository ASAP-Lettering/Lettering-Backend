package com.asap.bootstrap.integration.image

import com.asap.bootstrap.IntegrationSupporter
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.multipart

class ImageApiIntegrationTest : IntegrationSupporter() {
    @Test
    fun uploadImage() {
        // given
        val userId = userMockManager.settingUser()
        val accessToken = jwtMockManager.generateAccessToken(userId)
        val mockFile = MockMultipartFile("image", "test.jpg", "image/jpeg", "test".toByteArray())
        // when
        val response =
            mockMvc.multipart("/api/v1/images") {
                file(mockFile)
                contentType = MediaType.MULTIPART_FORM_DATA
                header("Authorization", "Bearer $accessToken")
            }
        // then
        response.andExpect {
            status { isOk() }
            jsonPath("$.imageUrl") {
                exists()
                isString()
                isNotEmpty()
            }
        }
    }
}
