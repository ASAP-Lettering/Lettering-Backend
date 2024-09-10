package com.asap.aws.s3

import com.asap.application.image.port.out.ImageManagementPort
import com.asap.application.image.vo.ImageMetadata
import com.asap.application.image.vo.UploadedImage
import io.awspring.cloud.s3.S3Template
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import software.amazon.awssdk.services.s3.presigner.S3Presigner

@TestConfiguration
class S3TestConfig {

    @Bean
    @Primary
    fun mockImageManagementAdapter(): ImageManagementPort{
        return object : ImageManagementPort {
            override fun save(image: ImageMetadata): UploadedImage {
                return UploadedImage(
                    imageUrl = "http://localhost:8080/image/${image.owner}/${image.fileMetaData.name}"
                )
            }
        }
    }

    @MockBean
    lateinit var s3Template: S3Template

    @MockBean
    lateinit var s3Presigner: S3Presigner


}