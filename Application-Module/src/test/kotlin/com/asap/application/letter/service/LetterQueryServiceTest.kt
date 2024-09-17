package com.asap.application.letter.service

import com.asap.application.letter.port.`in`.GetIndependentLettersUsecase
import com.asap.application.letter.port.`in`.GetVerifiedLetterUsecase
import com.asap.application.letter.port.out.IndependentLetterManagementPort
import com.asap.application.letter.port.out.SendLetterManagementPort
import com.asap.application.letter.port.out.SpaceLetterManagementPort
import com.asap.application.user.port.out.UserManagementPort
import com.asap.domain.common.DomainId
import com.asap.domain.letter.entity.IndependentLetter
import com.asap.domain.letter.entity.SendLetter
import com.asap.domain.letter.vo.LetterContent
import com.asap.domain.letter.vo.ReceiverInfo
import com.asap.domain.letter.vo.SenderInfo
import com.asap.domain.user.entity.User
import com.asap.domain.user.vo.UserPermission
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDate

class LetterQueryServiceTest : BehaviorSpec({

    val mockSendLetterManagementPort = mockk<SendLetterManagementPort>(relaxed = true)
    val mockUserManagementPort = mockk<UserManagementPort>(relaxed = true)
    val mockIndependentLetterManagementPort = mockk<IndependentLetterManagementPort>(relaxed = true)
    val mockSpaceLetterManagementPort = mockk<SpaceLetterManagementPort>(relaxed = true)

    val letterQueryService = LetterQueryService(
        mockSendLetterManagementPort,
        mockUserManagementPort,
        mockIndependentLetterManagementPort,
        mockSpaceLetterManagementPort
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
}) {
}