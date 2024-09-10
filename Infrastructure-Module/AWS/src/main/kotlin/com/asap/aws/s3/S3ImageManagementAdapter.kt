package com.asap.aws.s3

import com.asap.application.image.port.out.ImageManagementPort
import com.asap.application.image.vo.ImageMetadata
import com.asap.application.image.vo.UploadedImage
import io.awspring.cloud.s3.ObjectMetadata
import io.awspring.cloud.s3.S3Template
import org.springframework.stereotype.Component
import java.util.*

@Component
class S3ImageManagementAdapter(
    private val s3Template: S3Template
) : ImageManagementPort {


    override fun save(image: ImageMetadata): UploadedImage {
        val key = "${image.owner}/${UUID.randomUUID()}"

        val resource = s3Template.upload(
            BUCKET_NAME,
            key,
            image.fileMetaData.inputStream,
            ObjectMetadata.builder()
                .contentType(image.fileMetaData.contentType)
                .acl("public-read")
                .build()
        )
        return UploadedImage(
            imageUrl = resource.url.toString()
        )
    }



    companion object {
        private const val BUCKET_NAME = "lettering-images"
    }
}