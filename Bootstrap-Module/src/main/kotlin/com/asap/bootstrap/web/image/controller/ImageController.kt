package com.asap.bootstrap.web.image.controller

import com.asap.application.image.port.`in`.UploadImageUsecase
import com.asap.bootstrap.common.util.FileConverter
import com.asap.bootstrap.web.image.api.ImageApi
import com.asap.bootstrap.web.image.dto.UploadImageResponse
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class ImageController(
    private val uploadImageUsecase: UploadImageUsecase,
    private val fileConverter: FileConverter,
) : ImageApi {
    override fun uploadImage(
        image: MultipartFile,
        userId: String?,
    ): UploadImageResponse {
        val response =
            uploadImageUsecase.upload(
                UploadImageUsecase.Command(
                    image = fileConverter.convert(image),
                    userId = userId,
                ),
            )
        return UploadImageResponse(
            imageUrl = response.imageUrl,
        )
    }
}
