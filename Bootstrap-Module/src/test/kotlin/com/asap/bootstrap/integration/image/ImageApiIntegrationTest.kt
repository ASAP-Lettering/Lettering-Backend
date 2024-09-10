package com.asap.bootstrap.integration.image

import com.asap.application.user.UserMockManager
import com.asap.bootstrap.IntegrationSupporter
import com.asap.security.jwt.TestJwtDataGenerator
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.multipart

class ImageApiIntegrationTest: IntegrationSupporter() {

    @Autowired
    lateinit var testJwtDataGenerator: TestJwtDataGenerator

    @Autowired
    lateinit var userMockManager: UserMockManager



    @Test
    fun uploadImage() {
        //given
        val userId = userMockManager.settingUser()
        val accessToken = testJwtDataGenerator.generateAccessToken(userId)
        userMockManager.settingToken(accessToken)
        val mockFile = MockMultipartFile("image", "test.jpg", "image/jpeg", "test".toByteArray())
        //when
        val response = mockMvc.multipart("/api/v1/images") {
            file(mockFile)
            contentType = MediaType.MULTIPART_FORM_DATA
            header("Authorization", "Bearer $accessToken")
        }
        //then
        response.andExpect {
            status { isOk() }
            jsonPath("$.imageUrl") {
                exists()
                isString()
                isNotEmpty()
            }
        }.andDo {
            print()
        }
    }
}