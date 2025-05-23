package com.asap.application.letter.service

import com.asap.application.letter.port.`in`.*
import com.asap.application.letter.port.out.IndependentLetterManagementPort
import com.asap.application.letter.port.out.SendLetterManagementPort
import com.asap.application.letter.port.out.SpaceLetterManagementPort
import com.asap.application.space.port.out.SpaceManagementPort
import com.asap.application.user.port.out.UserManagementPort
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
        val mockUserManagementPort = mockk<UserManagementPort>(relaxed = true)
        val mockIndependentLetterManagementPort = mockk<IndependentLetterManagementPort>(relaxed = true)
        val mockSpaceLetterManagementPort = mockk<SpaceLetterManagementPort>(relaxed = true)
        val mockSpaceManagementPort = mockk<SpaceManagementPort>(relaxed = true)

        val letterQueryService =
            LetterQueryService(
                mockSendLetterManagementPort,
                mockUserManagementPort,
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
            val mockSendLetter = LetterFixture.generateSendLetter(user.id)
            val mockSender = UserFixture.createUser(mockSendLetter.senderId!!, "sender-name")
            every {
                mockSendLetterManagementPort.getReadLetterNotNull(
                    receiverId = DomainId(query.userId),
                    letterId = DomainId(query.letterId),
                )
            } returns mockSendLetter
            every {
                mockUserManagementPort.getUserNotNull(mockSender.id)
            } returns mockSender
            `when`("편지가 존재하면") {
                val response = letterQueryService.get(query)
                then("편지 정보를 가져와야 한다") {
                    response.senderName shouldBe mockSender.username
                    response.content shouldBe mockSendLetter.content.content
                    response.sendDate shouldBe mockSendLetter.createdDate
                    response.templateType shouldBe mockSendLetter.content.templateType
                    response.images shouldBe mockSendLetter.content.images
                }
            }
        }

        given("모든 무소속 편지를 조회할 떄") {
            val queryAll =
                GetIndependentLettersUsecase.QueryAll(
                    userId = "user-id",
                )
            val mockLetters =
                listOf(
                    LetterFixture.generateIndependentLetter(
                        senderId = DomainId.generate(),
                        senderName = "sender-name",
                        receiverId = DomainId(queryAll.userId),
                    ),
                )
            every {
                mockIndependentLetterManagementPort.getAllByReceiverId(DomainId(queryAll.userId))
            } returns mockLetters
            `when`("편지가 존재하면") {
                val response = letterQueryService.getAll(queryAll)
                then("편지 정보를 가져와야 한다") {
                    response.letters[0].letterId shouldBe mockLetters[0].id.value
                    response.letters[0].senderName shouldBe mockLetters[0].sender.senderName
                    response.letters[0].isNew shouldBe mockLetters[0].isNew()
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
            val spaceLetter = LetterFixture.generateSpaceLetter(receiverId = DomainId(query.userId), spaceId = space.id)
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
            `when`("편지가 존재하면") {
                val response = letterQueryService.get(query)
                then("편지 정보를 가져와야 한다") {
                    response.senderName shouldBe spaceLetter.sender.senderName
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
        }

        given("궤도 편지 상세 정보를 조회할 때") {
            val query =
                GetIndependentLettersUsecase.Query(
                    userId = "user-id",
                    letterId = "letter-id",
                )
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
            `when`("편지가 존재하면") {
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
