package com.asap.application.image.port.out

import com.asap.application.image.vo.ImageMetadata
import com.asap.application.image.vo.UploadedImage

interface ImageManagementPort {

    fun save(image: ImageMetadata): UploadedImage
}