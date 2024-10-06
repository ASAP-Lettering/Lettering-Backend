package com.asap.application.letter.service

import com.asap.application.letter.port.`in`.GenerateDraftKeyUsecase
import com.asap.application.letter.port.out.DraftLetterManagementPort
import com.asap.domain.common.DomainId
import com.asap.domain.letter.entity.DraftLetter
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.mockk.every
import io.mockk.mockk

class DraftLetterCommandServiceTest :
    BehaviorSpec({

        val mockGenerateDraftKeyUsecase = mockk<DraftLetterManagementPort>()
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
    })
