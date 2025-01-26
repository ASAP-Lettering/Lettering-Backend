package com.asap.application.letter.service

import com.asap.application.letter.exception.LetterException
import com.asap.application.letter.port.`in`.GetDraftLetterUsecase
import com.asap.application.letter.port.`in`.GetPhysicalDraftLetterUsecase
import com.asap.application.letter.port.out.DraftLetterManagementPort
import com.asap.application.letter.port.out.ReceiveDraftLetterManagementPort
import com.asap.domain.common.DomainId
import com.asap.domain.letter.entity.DraftLetter
import com.asap.domain.letter.entity.ReceiveDraftLetter
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class DraftLetterQueryServiceTest :
    BehaviorSpec({

        val mockDraftLetterManagementPort = mockk<DraftLetterManagementPort>()
        val mockReceiveLetterManagementPort = mockk<ReceiveDraftLetterManagementPort>()

        val draftLetterQueryService =
            DraftLetterQueryService(mockDraftLetterManagementPort, mockReceiveLetterManagementPort)

        given("임시 저장 편지를 조회할 떄") {
            val userId = "userId"
            val query = GetDraftLetterUsecase.Query.All(userId)
            val draftLetters = listOf(DraftLetter.default(DomainId(userId)))
            every { mockDraftLetterManagementPort.getAllDrafts(DomainId(userId)) } returns draftLetters
            `when`("사용자 아이디를 입력하면") {
                val response = draftLetterQueryService.getAll(query)
                then("임시 저장 편지 목록을 반환한다") {
                    response.shouldNotBeNull {
                        this.drafts.size shouldBe draftLetters.size
                        this.drafts.forEachIndexed { index, draftLetter ->
                            draftLetter.draftKey shouldBe draftLetters[index].id.value
                        }
                    }
                }
            }

            val draftLetter = DraftLetter.default(DomainId(userId))
            every {
                mockDraftLetterManagementPort.getDraftLetterNotNull(
                    draftId = draftLetter.id,
                    ownerId = DomainId(userId),
                )
            } returns draftLetter
            `when`("사용자 아이디와 임시 저장 편지 아이디를 입력하면") {
                val response =
                    draftLetterQueryService.getByKey(GetDraftLetterUsecase.Query.ByKey(draftLetter.id.value, userId))
                then("임시 저장 편지를 반환한다") {
                    response.shouldNotBeNull {
                        this.draftKey shouldBe draftLetter.id.value
                    }
                }
            }

            `when`("임시 저장 편지가 없다면") {
                every {
                    mockDraftLetterManagementPort.getDraftLetterNotNull(
                        draftId = draftLetter.id,
                        ownerId = DomainId(userId),
                    )
                } throws LetterException.DraftLetterNotFoundException()
                then("임시 저장 편지를 찾을 수 없다는 예외를 반환한다") {
                    shouldThrow<LetterException.DraftLetterNotFoundException> {
                        draftLetterQueryService.getByKey(
                            GetDraftLetterUsecase.Query.ByKey(
                                draftLetter.id.value,
                                userId
                            )
                        )
                    }
                }
            }
        }

        given("임시 저장 편지 개수를 조회할 때") {
            val userId = "userId"
            val query = GetDraftLetterUsecase.Query.All(userId)
            val draftCount = 1
            every { mockDraftLetterManagementPort.countDrafts(DomainId(userId)) } returns draftCount
            `when`("사용자 아이디를 입력하면") {
                val response = draftLetterQueryService.count(query)
                then("임시 저장 편지 개수를 반환한다") {
                    response.count shouldBe draftCount
                }
            }
        }

        given("임시 저장한 실물 편지를 조회할 때"){
            val userId = "userId"
            val query = GetPhysicalDraftLetterUsecase.Query.All(userId)
            val draftLetters = listOf(ReceiveDraftLetter.default(DomainId(userId)))
            every { mockReceiveLetterManagementPort.getAllDrafts(DomainId(userId)) } returns draftLetters
            `when`("사용자 아이디를 입력하면") {
                val response = draftLetterQueryService.getAll(query)
                then("임시 저장한 실물 편지 목록을 반환한다") {
                    response.shouldNotBeNull {
                        this.drafts.size shouldBe draftLetters.size
                        this.drafts.forEachIndexed { index, draftLetter ->
                            draftLetter.draftKey shouldBe draftLetters[index].id.value
                        }
                    }
                }
            }

            val draftLetter = ReceiveDraftLetter.default(DomainId(userId))
            every {
                mockReceiveLetterManagementPort.getDraftLetterNotNull(
                    draftId = draftLetter.id,
                    ownerId = DomainId(userId),
                )
            } returns draftLetter
            `when`("사용자 아이디와 임시 저장한 실물 편지 아이디를 입력하면") {
                val response =
                    draftLetterQueryService.getByKey(GetPhysicalDraftLetterUsecase.Query.ByKey(draftLetter.id.value, userId))
                then("임시 저장한 실물 편지를 반환한다") {
                    response.shouldNotBeNull {
                        this.draftKey shouldBe draftLetter.id.value
                    }
                }
            }

            `when`("임시 저장한 실물 편지가 없다면") {
                every {
                    mockReceiveLetterManagementPort.getDraftLetterNotNull(
                        draftId = draftLetter.id,
                        ownerId = DomainId(userId),
                    )
                } throws LetterException.DraftLetterNotFoundException()
                then("임시 저장한 실물 편지를 찾을 수 없다는 예외를 반환한다") {
                    shouldThrow<LetterException.DraftLetterNotFoundException> {
                        draftLetterQueryService.getByKey(
                            GetPhysicalDraftLetterUsecase.Query.ByKey(
                                draftLetter.id.value,
                                userId
                            )
                        )
                    }
                }
            }
        }
    })
