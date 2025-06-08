package com.asap.application.letter.service

import com.asap.application.letter.port.`in`.*
import com.asap.application.letter.port.out.IndependentLetterManagementPort
import com.asap.application.letter.port.out.SendLetterManagementPort
import com.asap.application.letter.port.out.SpaceLetterManagementPort
import com.asap.application.space.port.out.SpaceManagementPort
import com.asap.domain.LetterFixture
import com.asap.domain.SpaceFixture
import com.asap.domain.UserFixture
import com.asap.domain.common.DomainId
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class LetterQueryServiceTest :
    BehaviorSpec({

        val mockSendLetterManagementPort = mockk<SendLetterManagementPort>(relaxed = true)
        val mockIndependentLetterManagementPort = mockk<IndependentLetterManagementPort>(relaxed = true)
        val mockSpaceLetterManagementPort = mockk<SpaceLetterManagementPort>(relaxed = true)
        val mockSpaceManagementPort = mockk<SpaceManagementPort>(relaxed = true)

        val letterQueryService =
            LetterQueryService(
                mockSendLetterManagementPort,
                mockIndependentLetterManagementPort,
                mockSpaceLetterManagementPort,
                mockSpaceManagementPort,
            )

        given("검증된 편지를 가져올 때") {
            val user = UserFixture.createUser()
            val query =
                GetVerifiedLetterUsecase.Query(
                    letterId = "letter-id",
                    userId = user.id.value,
                )
            
            `when`("회원이 보낸 편지가 존재하면") {
                val mockSendLetter = LetterFixture.generateSendLetter(user.id)
                every {
                    mockSendLetterManagementPort.getReadLetterNotNull(
                        receiverId = DomainId(query.userId),
                        letterId = DomainId(query.letterId),
                    )
                } returns mockSendLetter
                
                val response = letterQueryService.get(query)
                then("발신자 이름은 mapper에서 처리된 이름이어야 한다") {
                    response.senderName shouldBe mockSendLetter.senderName
                    response.content shouldBe mockSendLetter.content.content
                    response.sendDate shouldBe mockSendLetter.createdDate
                    response.templateType shouldBe mockSendLetter.content.templateType
                    response.images shouldBe mockSendLetter.content.images
                }
            }
            
            `when`("익명으로 보낸 편지가 존재하면") {
                val anonymousSenderName = "익명 발신자"
                val mockAnonymousLetter = LetterFixture.generateAnonymousSendLetter(
                    receiverId = user.id,
                    senderName = anonymousSenderName
                )
                every {
                    mockSendLetterManagementPort.getReadLetterNotNull(
                        receiverId = DomainId(query.userId),
                        letterId = DomainId(query.letterId),
                    )
                } returns mockAnonymousLetter
                
                val response = letterQueryService.get(query)
                then("발신자 이름은 저장된 senderName이어야 한다") {
                    response.senderName shouldBe anonymousSenderName
                    response.content shouldBe mockAnonymousLetter.content.content
                    response.sendDate shouldBe mockAnonymousLetter.createdDate
                    response.templateType shouldBe mockAnonymousLetter.content.templateType
                    response.images shouldBe mockAnonymousLetter.content.images
                }
            }
        }

        given("모든 무소속 편지를 조회할 떄") {
            val queryAll =
                GetIndependentLettersUsecase.QueryAll(
                    userId = "user-id",
                )
            
            `when`("회원과 비회원 편지가 함께 존재하면") {
                val memberLetter = LetterFixture.generateIndependentLetter(
                    senderId = DomainId.generate(),
                    senderName = "회원 발신자",
                    receiverId = DomainId(queryAll.userId),
                )
                val anonymousLetter = LetterFixture.generateIndependentLetter(
                    senderId = null,
                    senderName = "비회원 발신자",
                    receiverId = DomainId(queryAll.userId),
                )
                val mockLetters = listOf(memberLetter, anonymousLetter)
                
                every {
                    mockIndependentLetterManagementPort.getAllByReceiverId(DomainId(queryAll.userId))
                } returns mockLetters
                
                val response = letterQueryService.getAll(queryAll)
                then("모든 편지의 발신자 이름이 정상적으로 표시되어야 한다") {
                    response.letters.size shouldBe 2
                    val senderNames = response.letters.map { it.senderName }.toSet()
                    senderNames shouldBe setOf("회원 발신자", "비회원 발신자")
                }
            }
        }

        given("편지 상세 정보를 조회할 때") {
            val query =
                GetSpaceLetterDetailUsecase.Query(
                    letterId = "letter-id",
                    userId = "user-id",
                )
            val space =
                SpaceFixture.createSpace(
                    userId = DomainId(query.userId),
                )
            
            `when`("회원이 보낸 행성 편지가 존재하면") {
                val spaceLetter = LetterFixture.generateSpaceLetter(
                    receiverId = DomainId(query.userId), 
                    spaceId = space.id,
                    senderName = "회원 발신자"
                )
                val prevSpaceLetter =
                    LetterFixture.generateSpaceLetter(receiverId = DomainId(query.userId), spaceId = space.id)
                val nextSpaceLetter =
                    LetterFixture.generateSpaceLetter(receiverId = DomainId(query.userId), spaceId = space.id)
                every {
                    mockSpaceLetterManagementPort.getSpaceLetterNotNull(
                        DomainId(query.letterId),
                        DomainId(query.userId),
                    )
                } returns spaceLetter
                every {
                    mockSpaceManagementPort.getSpaceNotNull(
                        spaceLetter.receiver.receiverId,
                        spaceLetter.spaceId,
                    )
                } returns space
                every {
                    mockSpaceLetterManagementPort.countSpaceLetterBy(
                        spaceLetter.spaceId,
                        spaceLetter.receiver.receiverId,
                    )
                } returns 3
                every {
                    mockSpaceLetterManagementPort.getNearbyLetter(
                        spaceId = spaceLetter.spaceId,
                        userId = spaceLetter.receiver.receiverId,
                        letterId = spaceLetter.id,
                    )
                } returns Pair(prevSpaceLetter, nextSpaceLetter)
                
                val response = letterQueryService.get(query)
                then("편지 정보를 가져와야 한다") {
                    response.senderName shouldBe "회원 발신자"
                    response.spaceName shouldBe space.name
                    response.letterCount shouldBe 3
                    response.content shouldBe spaceLetter.content.content
                    response.receiveDate shouldBe spaceLetter.receiveDate
                    response.images shouldBe spaceLetter.content.images
                    response.templateType shouldBe spaceLetter.content.templateType
                    response.prevLetter.shouldNotBeNull {
                        this.letterId shouldBe prevSpaceLetter.id.value
                        this.senderName shouldBe prevSpaceLetter.sender.senderName
                    }
                    response.nextLetter.shouldNotBeNull {
                        this.letterId shouldBe nextSpaceLetter.id.value
                        this.senderName shouldBe nextSpaceLetter.sender.senderName
                    }
                }
            }
            
            `when`("비회원이 보낸 행성 편지가 존재하면") {
                val anonymousSpaceLetter = LetterFixture.generateSpaceLetter(
                    receiverId = DomainId(query.userId), 
                    spaceId = space.id,
                    senderId = null,
                    senderName = "비회원 발신자"
                )
                every {
                    mockSpaceLetterManagementPort.getSpaceLetterNotNull(
                        DomainId(query.letterId),
                        DomainId(query.userId),
                    )
                } returns anonymousSpaceLetter
                every {
                    mockSpaceManagementPort.getSpaceNotNull(
                        anonymousSpaceLetter.receiver.receiverId,
                        anonymousSpaceLetter.spaceId,
                    )
                } returns space
                every {
                    mockSpaceLetterManagementPort.countSpaceLetterBy(
                        anonymousSpaceLetter.spaceId,
                        anonymousSpaceLetter.receiver.receiverId,
                    )
                } returns 1
                every {
                    mockSpaceLetterManagementPort.getNearbyLetter(
                        spaceId = anonymousSpaceLetter.spaceId,
                        userId = anonymousSpaceLetter.receiver.receiverId,
                        letterId = anonymousSpaceLetter.id,
                    )
                } returns Pair(null, null)
                
                val response = letterQueryService.get(query)
                then("비회원 발신자 이름이 정상적으로 표시되어야 한다") {
                    response.senderName shouldBe "비회원 발신자"
                    response.spaceName shouldBe space.name
                    response.letterCount shouldBe 1
                    response.content shouldBe anonymousSpaceLetter.content.content
                    response.receiveDate shouldBe anonymousSpaceLetter.receiveDate
                    response.images shouldBe anonymousSpaceLetter.content.images
                    response.templateType shouldBe anonymousSpaceLetter.content.templateType
                    response.prevLetter shouldBe null
                    response.nextLetter shouldBe null
                }
            }
        }

        given("궤도 편지 상세 정보를 조회할 때") {
            val query =
                GetIndependentLettersUsecase.Query(
                    userId = "user-id",
                    letterId = "letter-id",
                )
            
            `when`("회원이 보낸 독립 편지가 존재하면") {
                val independentLetter =
                    LetterFixture.generateIndependentLetter(
                        senderId = DomainId.generate(),
                        senderName = "sender-name",
                        receiverId = DomainId(query.userId),
                    )
                val prevIndependentLetter =
                    LetterFixture.generateIndependentLetter(
                        senderId = DomainId.generate(),
                        senderName = "prev-sender-name",
                        receiverId = DomainId(query.userId),
                    )

                val nextIndependentLetter =
                    LetterFixture.generateIndependentLetter(
                        senderId = DomainId.generate(),
                        senderName = "next-sender-name",
                        receiverId = DomainId(query.userId),
                    )
                every {
                    mockIndependentLetterManagementPort.getIndependentLetterByIdNotNull(
                        DomainId(query.letterId),
                        DomainId(query.userId),
                    )
                } returns independentLetter
                every {
                    mockIndependentLetterManagementPort.getNearbyLetter(
                        userId = DomainId(query.userId),
                        letterId = DomainId(query.letterId),
                    )
                } returns Pair(prevIndependentLetter, nextIndependentLetter)
                every {
                    mockIndependentLetterManagementPort.countIndependentLetterByReceiverId(DomainId(query.userId))
                } returns 3
                
                val response = letterQueryService.get(query)
                then("편지 정보를 가져와야 한다") {
                    response.senderName shouldBe independentLetter.sender.senderName
                    response.letterCount shouldBe 3
                    response.content shouldBe independentLetter.content.content
                    response.sendDate shouldBe independentLetter.receiveDate
                    response.images shouldBe independentLetter.content.images
                    response.templateType shouldBe independentLetter.content.templateType
                    response.prevLetter.shouldNotBeNull {
                        this.letterId shouldBe prevIndependentLetter.id.value
                        this.senderName shouldBe prevIndependentLetter.sender.senderName
                    }
                    response.nextLetter.shouldNotBeNull {
                        this.letterId shouldBe nextIndependentLetter.id.value
                        this.senderName shouldBe nextIndependentLetter.sender.senderName
                    }
                }
            }
            
            `when`("비회원이 보낸 독립 편지가 존재하면") {
                val anonymousIndependentLetter =
                    LetterFixture.generateIndependentLetter(
                        senderId = null,
                        senderName = "비회원 발신자",
                        receiverId = DomainId(query.userId),
                    )
                every {
                    mockIndependentLetterManagementPort.getIndependentLetterByIdNotNull(
                        DomainId(query.letterId),
                        DomainId(query.userId),
                    )
                } returns anonymousIndependentLetter
                every {
                    mockIndependentLetterManagementPort.getNearbyLetter(
                        userId = DomainId(query.userId),
                        letterId = DomainId(query.letterId),
                    )
                } returns Pair(null, null)
                every {
                    mockIndependentLetterManagementPort.countIndependentLetterByReceiverId(DomainId(query.userId))
                } returns 1
                
                val response = letterQueryService.get(query)
                then("비회원 발신자 이름이 정상적으로 표시되어야 한다") {
                    response.senderName shouldBe "비회원 발신자"
                    response.letterCount shouldBe 1
                    response.content shouldBe anonymousIndependentLetter.content.content
                    response.sendDate shouldBe anonymousIndependentLetter.receiveDate
                    response.images shouldBe anonymousIndependentLetter.content.images
                    response.templateType shouldBe anonymousIndependentLetter.content.templateType
                    response.prevLetter shouldBe null
                    response.nextLetter shouldBe null
                }
            }
        }

        given("전체 편지 조회 요청이 들어올 떄") {
            val independentLetterCount = 10L
            val spaceLetterCount = 20L
            val query = GetAllLetterCountUsecase.Query("userId")
            every { mockIndependentLetterManagementPort.countIndependentLetterByReceiverId(DomainId(query.userId)) } returns
                independentLetterCount
            every { mockSpaceLetterManagementPort.countAllSpaceLetterBy(DomainId(query.userId)) } returns spaceLetterCount
            every { mockSpaceManagementPort.countByUserId(DomainId(query.userId)) } returns 10
            `when`("유저 아이디가 주어진다면") {
                val response = letterQueryService.get(query)
                then("전체 편지 개수를 반환한다") {
                    response.letterCount shouldBe independentLetterCount + spaceLetterCount
                    response.spaceCount shouldBe 10
                }
            }
        }

        given("작성한 편지에 대해 조회할 떄") {
            val sender = UserFixture.createUser()
            val query = GetSendLetterUsecase.Query.AllHistory(sender.id.value)
            val sendLetter = LetterFixture.generateSendLetter(senderId = sender.id)
            `when`("전체 편지 조회 요청이 들어오면") {
                every { mockSendLetterManagementPort.getAllBy(sender.id) } returns listOf(sendLetter)
                val response = letterQueryService.getHistory(query)
                then("전체 편지를 반환한다") {
                    response[0].letterId shouldBe sendLetter.id.value
                    response[0].receiverName shouldBe sendLetter.receiverName
                    response[0].sendDate shouldBe sendLetter.createdDate
                }
            }

            `when`("상세 편지 조회 요청이 들어오면") {
                val queryDetail = GetSendLetterUsecase.Query.Detail(sender.id.value, sendLetter.id.value)
                every {
                    mockSendLetterManagementPort.getSendLetterBy(
                        letterId = DomainId(queryDetail.letterId),
                        senderId = DomainId(queryDetail.userId),
                    )
                } returns sendLetter
                val response = letterQueryService.getDetail(queryDetail)
                then("상세 편지를 반환한다") {
                    response.receiverName shouldBe sendLetter.receiverName
                    response.sendDate shouldBe sendLetter.createdDate
                    response.content shouldBe sendLetter.content.content
                    response.images shouldBe sendLetter.content.images
                    response.templateType shouldBe sendLetter.content.templateType
                }
            }
        }
    })
