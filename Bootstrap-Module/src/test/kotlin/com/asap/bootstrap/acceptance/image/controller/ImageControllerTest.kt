package com.asap.bootstrap.acceptance.image.controller

import com.asap.application.image.port.`in`.UploadImageUsecase
import com.asap.bootstrap.AcceptanceSupporter
import com.asap.bootstrap.common.util.FileConverter
import com.asap.bootstrap.image.controller.ImageController
import com.asap.common.file.FileMetaData
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.multipart

@WebMvcTest(ImageController::class)
class ImageControllerTest : AcceptanceSupporter() {

    @MockBean
    lateinit var uploadImageUsecase: UploadImageUsecase

    @MockBean
    lateinit var fileConverter: FileConverter

    @Test
    fun uploadImage() {
        //given
        val accessToken = testJwtDataGenerator.generateAccessToken()
        val mockFile = MockMultipartFile("image", "test.jpg", "image/jpeg", "test".toByteArray())
        val mockFileMetaData = FileMetaData("test.jpg", 4, "image/jpeg", mockFile.inputStream)
        BDDMockito.given(fileConverter.convert(mockFile))
            .willReturn(mockFileMetaData)
        BDDMockito.given(
            uploadImageUsecase.upload(
                UploadImageUsecase.Command(
                    image = mockFileMetaData,
                    "userId"
                )
            )
        ).willReturn(UploadImageUsecase.Response("imageUrl"))
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
        }
    }
}