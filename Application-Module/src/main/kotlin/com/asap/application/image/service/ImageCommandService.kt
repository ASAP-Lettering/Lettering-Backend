package com.asap.application.image.service

import com.asap.application.image.port.`in`.UploadImageUsecase
import com.asap.application.image.port.out.ImageManagementPort
import com.asap.application.image.vo.ImageMetadata
import com.asap.application.user.port.out.UserManagementPort
import com.asap.domain.common.DomainId
import org.springframework.stereotype.Service

@Service
class ImageCommandService(
    private val imageManagementPort: ImageManagementPort,
    private val userManagementPort: UserManagementPort,
) : UploadImageUsecase {
    override fun upload(command: UploadImageUsecase.Command): UploadImageUsecase.Response {
        val user = command.userId?.let { userManagementPort.getUserNotNull(DomainId(it)) }

        val uploadedImage =
            imageManagementPort.save(
                ImageMetadata(
                    owner = user?.id?.value,
                    fileMetaData = command.image,
                ),
            )
        return UploadImageUsecase.Response(
            imageUrl = uploadedImage.imageUrl,
        )
    }
}
