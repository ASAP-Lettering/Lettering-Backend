package com.asap.application.image.port.out.memory

import com.asap.application.image.port.out.ImageManagementPort
import com.asap.application.image.vo.ImageMetadata
import com.asap.application.image.vo.UploadedImage
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class MemoryImageManagementAdapter(

): ImageManagementPort {

    private val images = ConcurrentHashMap<String, String>()


    override fun save(image: ImageMetadata): UploadedImage {
        val imageUrl = "http://localhost:8080/image/${image.owner}/${image.fileMetaData.name}"
        return UploadedImage(
            imageUrl = imageUrl
        ).also {
            images[image.owner] = imageUrl
        }
    }

}