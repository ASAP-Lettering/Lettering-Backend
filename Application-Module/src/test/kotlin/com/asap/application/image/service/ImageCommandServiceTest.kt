package com.asap.application.image.service

import com.asap.application.image.port.`in`.UploadImageUsecase
import com.asap.application.image.port.out.ImageManagementPort
import com.asap.application.image.vo.ImageMetadata
import com.asap.application.image.vo.UploadedImage
import com.asap.application.user.port.out.UserManagementPort
import com.asap.common.file.FileMetaData
import com.asap.domain.UserFixture
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.io.InputStream

class ImageCommandServiceTest :
    BehaviorSpec({

        val mockImageManagementPort = mockk<ImageManagementPort>(relaxed = true)
        val mockUserManagementPort = mockk<UserManagementPort>(relaxed = true)

        val imageCommandService =
            ImageCommandService(
                mockImageManagementPort,
                mockUserManagementPort,
            )

        given("익명 사용자의 이미지 업로드 요청이 들어올 때") {
            val command =
                UploadImageUsecase.Command(
                    userId = null,
                    image =
                        FileMetaData(
                            name = "name",
                            contentType = "contentType",
                            size = 1L,
                            inputStream = InputStream.nullInputStream(),
                        ),
                )
            val imageMetadataSlot = slot<ImageMetadata>()
            every {
                mockImageManagementPort.save(capture(imageMetadataSlot))
            } returns
                UploadedImage(
                    imageUrl = "anonymousImageUrl",
                )
            `when`("이미지 업로드 요청을 처리하면") {
                val response = imageCommandService.upload(command)
                then("이미지가 저장되어야 한다") {
                    response.imageUrl shouldNotBeNull {
                        this.isNotBlank()
                        this.isNotEmpty()
                    }
                    verify { mockImageManagementPort.save(any()) }
                    imageMetadataSlot.captured.owner shouldBe "anonymous"
                }
            }
        }

        given("이미지 업로드 요청이 들어올 때") {
            val mockUser = UserFixture.createUser()
            val command =
                UploadImageUsecase.Command(
                    userId = mockUser.id.value,
                    image =
                        FileMetaData(
                            name = "name",
                            contentType = "contentType",
                            size = 1L,
                            inputStream = InputStream.nullInputStream(),
                        ),
                )
            every {
                mockUserManagementPort.getUserNotNull(any())
            } returns mockUser
            every {
                mockImageManagementPort.save(any())
            } returns
                UploadedImage(
                    imageUrl = "imageUrl",
                )
            `when`("이미지 업로드 요청을 처리하면") {
                val response = imageCommandService.upload(command)
                then("이미지가 저장되어야 한다") {
                    response.imageUrl shouldNotBeNull {
                        this.isNotBlank()
                        this.isNotEmpty()
                    }
                }
            }
        }
    })
