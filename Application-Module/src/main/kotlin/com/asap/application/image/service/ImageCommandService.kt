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
    companion object {
        private const val ANONYMOUS_OWNER_ID = "anonymous"
    }

    override fun upload(command: UploadImageUsecase.Command): UploadImageUsecase.Response {
        val owner =
            when {
                command.userId != null -> {
                    val user = userManagementPort.getUserNotNull(DomainId(command.userId))
                    user.id.value
                }

                else -> ANONYMOUS_OWNER_ID
            }

        val uploadedImage =
            imageManagementPort.save(
                ImageMetadata(
                    owner = owner,
                    fileMetaData = command.image,
                ),
            )
        return UploadImageUsecase.Response(
            imageUrl = uploadedImage.imageUrl,
        )
    }
}
