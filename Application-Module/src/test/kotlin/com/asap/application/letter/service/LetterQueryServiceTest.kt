package com.asap.application.letter.service

import com.asap.application.letter.port.`in`.GetIndependentLettersUsecase
import com.asap.application.letter.port.`in`.GetSpaceLetterDetailUsecase
import com.asap.application.letter.port.`in`.GetVerifiedLetterUsecase
import com.asap.application.letter.port.out.IndependentLetterManagementPort
import com.asap.application.letter.port.out.SendLetterManagementPort
import com.asap.application.letter.port.out.SpaceLetterManagementPort
import com.asap.application.space.port.out.SpaceManagementPort
import com.asap.application.user.port.out.UserManagementPort
import com.asap.domain.common.DomainId
import com.asap.domain.letter.entity.IndependentLetter
import com.asap.domain.letter.entity.SendLetter
import com.asap.domain.letter.entity.SpaceLetter
import com.asap.domain.letter.vo.LetterContent
import com.asap.domain.letter.vo.ReceiverInfo
import com.asap.domain.letter.vo.SenderInfo
import com.asap.domain.space.entity.Space
import com.asap.domain.user.entity.User
import com.asap.domain.user.vo.UserPermission
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDate

class LetterQueryServiceTest : BehaviorSpec({

    val mockSendLetterManagementPort = mockk<SendLetterManagementPort>(relaxed = true)
    val mockUserManagementPort = mockk<UserManagementPort>(relaxed = true)
    val mockIndependentLetterManagementPort = mockk<IndependentLetterManagementPort>(relaxed = true)
    val mockSpaceLetterManagementPort = mockk<SpaceLetterManagementPort>(relaxed = true)
    val mockSpaceManagementPort = mockk<SpaceManagementPort>(relaxed = true)

    val letterQueryService = LetterQueryService(
        mockSendLetterManagementPort,
        mockUserManagementPort,
        mockIndependentLetterManagementPort,
        mockSpaceLetterManagementPort,
        mockSpaceManagementPort
    )


    given("검증된 편지를 가져올 때") {
        val query = GetVerifiedLetterUsecase.Query(
            letterId = "letter-id",
            userId = "user-id"
        )
        val mockSendLetter = SendLetter(
            id = DomainId(query.letterId),
            receiverName = "receiver-name",
            content = LetterContent(
                "content",
                images = listOf("image1", "image2"),
                templateType = 1
            ),
            senderId = DomainId.generate(),
            letterCode = "letter-code"
        )
        val mockSender = User(
            id = mockSendLetter.senderId,
            username = "sender-name",
            profileImage = "profile-image",
            permission = UserPermission(true, true, true),
            birthday = null
        )
        every {
            mockSendLetterManagementPort.getExpiredLetterNotNull(
                receiverId = DomainId(query.userId),
                letterId = DomainId(query.letterId)
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
        val query = GetIndependentLettersUsecase.Query(
            userId = "user-id"
        )
        val mockLetters = listOf(
            IndependentLetter(
                id = DomainId.generate(),
                sender = SenderInfo(
                    senderId = DomainId.generate(),
                    senderName = "sender-name"
                ),
                receiver = ReceiverInfo(
                    receiverId = DomainId(query.userId)
                ),
                isNew = true,
                content = LetterContent(
                    content = "content",
                    templateType = 1,
                    images = listOf("image1", "image2")
                ),
                receiveDate = LocalDate.now()
            )
        )
        every {
            mockIndependentLetterManagementPort.getAllByReceiverId(DomainId(query.userId))
        } returns mockLetters
        `when`("편지가 존재하면") {
            val response = letterQueryService.get(query)
            then("편지 정보를 가져와야 한다") {
                response.letters[0].letterId shouldBe mockLetters[0].id.value
                response.letters[0].senderName shouldBe mockLetters[0].sender.senderName
                response.letters[0].isNew shouldBe mockLetters[0].isNew
            }
        }
    }

    given("편지 상세 정보를 조회할 때") {
        val query = GetSpaceLetterDetailUsecase.Query(
            letterId = "letter-id",
            userId = "user-id"
        )
        val space = Space(
            id = DomainId.generate(),
            name = "space-name",
            userId = DomainId(query.userId),
            templateType = 1
        )
        val spaceLetter = SpaceLetter(
            id = DomainId(query.letterId),
            sender = SenderInfo(
                senderId = DomainId.generate(),
                senderName = "sender-name"
            ),
            receiver = ReceiverInfo(
                receiverId = DomainId(query.userId),
            ),
            content = LetterContent(
                content = "content",
                templateType = 1,
                images = listOf("image1", "image2")
            ),
            receiveDate = LocalDate.now(),
            spaceId = space.id
        )
        val prevSpaceLetter = SpaceLetter(
            id = DomainId.generate(),
            sender = SenderInfo(
                senderId = DomainId.generate(),
                senderName = "prev-sender-name"
            ),
            receiver = ReceiverInfo(
                receiverId = DomainId(query.userId),
            ),
            content = LetterContent(
                content = "prev-content",
                templateType = 1,
                images = listOf("prev-image1", "prev-image2")
            ),
            receiveDate = LocalDate.now(),
            spaceId = space.id
        )
        val nextSpaceLetter = SpaceLetter(
            id = DomainId.generate(),
            sender = SenderInfo(
                senderId = DomainId.generate(),
                senderName = "next-sender-name"
            ),
            receiver = ReceiverInfo(
                receiverId = DomainId(query.userId),
            ),
            content = LetterContent(
                content = "next-content",
                templateType = 1,
                images = listOf("next-image1", "next-image2")
            ),
            receiveDate = LocalDate.now(),
            spaceId = space.id
        )
        every {
            mockSpaceLetterManagementPort.getSpaceLetterNotNull(
                DomainId(query.letterId),
                DomainId(query.userId)
            )
        } returns spaceLetter
        every {
            mockSpaceManagementPort.getSpaceNotNull(
                spaceLetter.receiver.receiverId,
                spaceLetter.spaceId
            )
        } returns space
        every {
            mockSpaceLetterManagementPort.countLetterBySpaceId(spaceLetter.spaceId)
        } returns 3
        every {
            mockSpaceLetterManagementPort.getNearbyLetter(
                spaceId = spaceLetter.spaceId,
                userId = spaceLetter.receiver.receiverId,
                letterId = spaceLetter.id
            )
        } returns Pair(prevSpaceLetter, nextSpaceLetter)
        `when`("편지가 존재하면") {
            val response = letterQueryService.get(query)
            then("편지 정보를 가져와야 한다") {
                response.senderName shouldBe spaceLetter.sender.senderName
                response.spaceName shouldBe space.name
                response.letterCount shouldBe 3
                response.content shouldBe spaceLetter.content.content
                response.sendDate shouldBe spaceLetter.receiveDate
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

})