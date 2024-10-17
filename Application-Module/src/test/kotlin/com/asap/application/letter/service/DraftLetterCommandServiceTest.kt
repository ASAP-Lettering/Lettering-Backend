package com.asap.application.letter.service

import com.asap.application.letter.port.`in`.GenerateDraftKeyUsecase
import com.asap.application.letter.port.`in`.RemoveDraftLetterUsecase
import com.asap.application.letter.port.`in`.UpdateDraftLetterUsecase
import com.asap.application.letter.port.out.DraftLetterManagementPort
import com.asap.domain.common.DomainId
import com.asap.domain.letter.entity.DraftLetter
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class DraftLetterCommandServiceTest :
    BehaviorSpec({

        val mockGenerateDraftKeyUsecase = mockk<DraftLetterManagementPort>(relaxed = true)
        val draftLetterCommandService = DraftLetterCommandService(mockGenerateDraftKeyUsecase)

        given("임시 저장 키를 발급할 때") {
            val userId = "userId"
            val draftLetter = DraftLetter.default(DomainId(userId))
            every { mockGenerateDraftKeyUsecase.save(any()) } returns draftLetter
            `when`("사용자 아이디를 입력하면") {
                val response = draftLetterCommandService.command(GenerateDraftKeyUsecase.Command(userId))
                then("임시 저장 키를 발급한다") {
                    response.draftId.shouldNotBeNull()
                }
            }
        }

        given("임시 저장 편지를 수정할 때") {
            val command =
                UpdateDraftLetterUsecase.Command(
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
            val command = RemoveDraftLetterUsecase.Command.Draft(draftId = "draftId", userId = "userId")
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
    })
