package com.asap.application.image.port.`in`

import com.asap.common.file.FileMetaData

interface UploadImageUsecase {

    fun upload(command: Command): Response


    data class Command(
        val image: FileMetaData,
        val userId: String? = null
    )

    data class Response(
        val imageUrl: String
    )
}
