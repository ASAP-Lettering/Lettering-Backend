package com.asap.application.letter.service

import com.asap.application.letter.exception.LetterException
import com.asap.application.letter.port.`in`.GenerateDraftKeyUsecase
import com.asap.application.letter.port.`in`.RemoveDraftLetterUsecase
import com.asap.application.letter.port.`in`.UpdateDraftLetterUsecase
import com.asap.application.letter.port.out.DraftLetterManagementPort
import com.asap.application.letter.port.out.ReceiveDraftLetterManagementPort
import com.asap.domain.common.DomainId
import com.asap.domain.letter.entity.DraftLetter
import com.asap.domain.letter.entity.ReceiveDraftLetter
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class DraftLetterCommandServiceTest :
    BehaviorSpec({
        isolationMode = IsolationMode.InstancePerLeaf

        val mockGenerateDraftKeyUsecase = mockk<DraftLetterManagementPort>(relaxed = true)
        val mockReceiveDraftLetterManagementPort = mockk<ReceiveDraftLetterManagementPort>(relaxed = true)
        val draftLetterCommandService =
            DraftLetterCommandService(mockGenerateDraftKeyUsecase, mockReceiveDraftLetterManagementPort)

        given("임시 저장 키를 발급할 때") {
            val userId = "userId"
            val draftLetter = DraftLetter.default(DomainId(userId))
            every { mockGenerateDraftKeyUsecase.save(any()) } returns draftLetter
            `when`("사용자 아이디를 입력하면") {
                val response = draftLetterCommandService.command(GenerateDraftKeyUsecase.Command.Send(userId))
                then("임시 저장 키를 발급한다") {
                    response.draftId.shouldNotBeNull()
                }
            }
        }

        given("임시 저장 편지를 수정할 때") {
            val command =
                UpdateDraftLetterUsecase.Command.Send(
                    draftId = "draftId",
                    userId = "userId",
                    content = "content",
                    receiverName = "receiverName",
                    images = listOf("image1", "image2"),
                )
            val draftLetter = DraftLetter.default(DomainId(command.userId))
            every {
                mockGenerateDraftKeyUsecase.getDraftLetterNotNull(
                    draftId = draftLetter.id,
                    ownerId = draftLetter.ownerId,
                )
            } returns draftLetter
            `when`("사용자 아이디와 임시 저장 편지 아이디, 내용, 수신자 이름, 이미지를 입력하면") {
                draftLetterCommandService.command(command)
                then("임시 저장 편지를 수정한다") {
                    verify { mockGenerateDraftKeyUsecase.save(any()) }
                }
            }
        }

        given("임시 저장 편지를 삭제할 때") {
            val command = RemoveDraftLetterUsecase.Command.Send(draftId = "draftId", userId = "userId")
            val draftLetter = DraftLetter.default(DomainId(command.userId))
            every {
                mockGenerateDraftKeyUsecase.getDraftLetterNotNull(
                    draftId = draftLetter.id,
                    ownerId = draftLetter.ownerId,
                )
            } returns draftLetter
            `when`("사용자 아이디와 임시 저장 편지 아이디를 입력하면") {
                draftLetterCommandService.deleteBy(command)
                then("임시 저장 편지를 삭제한다") {
                    verify { mockGenerateDraftKeyUsecase.remove(any()) }
                }
            }
        }

        given("받은 편지를 임시저장하려 할때"){
            val command = GenerateDraftKeyUsecase.Command.Physical("userId")
            val receiveDraftLetter = ReceiveDraftLetter.default(DomainId(command.userId))
            every { mockReceiveDraftLetterManagementPort.save(any()) } returns receiveDraftLetter
            `when`("사용자 아이디를 입력하면"){
                val response = draftLetterCommandService.command(command)
                then("받은 편지를 임시저장한다"){
                    response.draftId.shouldNotBeNull()
                }
            }
        }

        given("받은 임시 저장편지를 수저할 때"){

            `when`("사용자 아이디와 임시 저장 편지 아이디, 내용, 발신자 이름, 이미지를 입력하면"){
                val command = UpdateDraftLetterUsecase.Command.Physical(
                    draftId = "draftId",
                    userId = "userId",
                    content = "content",
                    images = listOf("image1", "image2"),
                    senderName = "senderName",
                )
                val receiveDraftLetter = ReceiveDraftLetter.default(DomainId(command.userId))
                every {
                    mockReceiveDraftLetterManagementPort.getDraftLetterNotNull(
                        draftId = receiveDraftLetter.id,
                        ownerId = receiveDraftLetter.ownerId,
                    )
                } returns receiveDraftLetter
                draftLetterCommandService.command(command)
                then("받은 임시 저장편지를 수정한다"){
                    verify { mockReceiveDraftLetterManagementPort.save(any()) }
                }
            }

            `when`("임시 저장 편지가 없으면"){

                val command = UpdateDraftLetterUsecase.Command.Physical(
                    draftId = "draftId",
                    userId = "userId",
                    content = "content",
                    images = listOf("image1", "image2"),
                    senderName = "senderName",
                )
                every {
                    mockReceiveDraftLetterManagementPort.getDraftLetterNotNull(
                        draftId = DomainId(command.draftId),
                        ownerId = DomainId(command.userId),
                    )
                } throws LetterException.DraftLetterNotFoundException()
                then("임시 저장 편지가 수정되지 않는다"){
                    verify(exactly = 0) {
                        mockReceiveDraftLetterManagementPort.save(any())
                    }
                }
            }
        }

        given("받은 임시 저장편지를 삭제할 때"){

            `when`("사용자 아이디와 임시 저장 편지 아이디를 입력하면"){
                val command = RemoveDraftLetterUsecase.Command.Physical(draftId = "draftId", userId = "userId")
                val receiveDraftLetter = ReceiveDraftLetter.default(DomainId(command.userId))
                every {
                    mockReceiveDraftLetterManagementPort.getDraftLetterNotNull(
                        draftId = receiveDraftLetter.id,
                        ownerId = receiveDraftLetter.ownerId,
                    )
                } returns receiveDraftLetter
                draftLetterCommandService.deleteBy(command)
                then("받은 임시 저장편지를 삭제한다"){
                    verify { mockReceiveDraftLetterManagementPort.remove(any()) }
                }
            }

            `when`("임시 저장 편지가 없으면"){

                val command = RemoveDraftLetterUsecase.Command.Physical(draftId = "draftId", userId = "userId")
                every {
                    mockReceiveDraftLetterManagementPort.getDraftLetterNotNull(
                        draftId = DomainId(command.draftId),
                        ownerId = DomainId(command.userId),
                    )
                } throws LetterException.DraftLetterNotFoundException()
                then("임시 저장 편지가 삭제되지 않는다"){
                    verify(exactly = 0) {
                        mockReceiveDraftLetterManagementPort.remove(any())
                    }
                }
            }
        }
    })
