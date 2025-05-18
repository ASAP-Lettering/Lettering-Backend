package com.asap.application.image.vo

import com.asap.common.file.FileMetaData

data class ImageMetadata(
    val owner: String?,
    val fileMetaData: FileMetaData,
)
