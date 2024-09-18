package com.asap.application.letter.service

import com.asap.application.letter.exception.LetterException
import com.asap.application.letter.port.`in`.*
import com.asap.application.letter.port.out.IndependentLetterManagementPort
import com.asap.application.letter.port.out.SendLetterManagementPort
import com.asap.application.letter.port.out.SpaceLetterManagementPort
import com.asap.application.user.port.out.UserManagementPort
import com.asap.domain.common.DomainId
import com.asap.domain.letter.entity.IndependentLetter
import com.asap.domain.letter.entity.SendLetter
import com.asap.domain.letter.entity.SpaceLetter
import com.asap.domain.letter.vo.LetterContent
import com.asap.domain.letter.vo.ReceiverInfo
import com.asap.domain.letter.vo.SenderInfo
import com.asap.domain.user.entity.User
import com.asap.domain.user.vo.UserPermission
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDate


class LetterCommandServiceTest : BehaviorSpec({

    val mockSendLetterManagementPort = mockk<SendLetterManagementPort>(relaxed = true)
    val mockIndependentLetterManagementPort = mockk<IndependentLetterManagementPort>(relaxed = true)
    val mockUserManagementPort = mockk<UserManagementPort>(relaxed = true)
    val mockSpaceLetterManagementPort = mockk<SpaceLetterManagementPort>(relaxed = true)

    val letterCommandService = LetterCommandService(
        mockSendLetterManagementPort,
        mockIndependentLetterManagementPort,
        mockSpaceLetterManagementPort,
        mockUserManagementPort
    )



    given("편지 전송 요청이 들어올 때") {
        val command = SendLetterUsecase.Command(
            userId = "user-id",
            receiverName = "receiver-name",
            content = "content",
            images = emptyList(),
            templateType = 1,
            draftId = null
        )
        `when`("편지 전송 요청을 처리하면") {
            val response = letterCommandService.send(command)
            then("편지 코드가 생성되고, 편지가 저장되어야 한다") {
                response.letterCode shouldNotBeNull {
                    this.isNotBlank()
                    this.isNotEmpty()
                }
                verify { mockSendLetterManagementPort.save(any()) }
            }
        }
    }

    given("편지 검증 시에") {
        val letterCode = "letter-code"
        val verifyCommand = VerifyLetterAccessibleUsecase.Command(
            letterCode = letterCode,
            userId = "user-id"
        )
        val sendLetter = SendLetter(
            receiverName = "receiver-name",
            content = LetterContent(
                "content",
                images = emptyList(),
                templateType = 1,
            ),
            senderId = mockk(),
            letterCode = letterCode
        )
        val mockUser = User(
            id = DomainId("user-id"),
            username = "receiver-name",
            profileImage = "profile-image",
            permission = UserPermission(true, true, true),
            birthday = LocalDate.now()
        )
        every { mockSendLetterManagementPort.verifiedLetter(any(), letterCode) } returns false
        every { mockSendLetterManagementPort.getLetterByCodeNotNull(any()) } returns sendLetter
        every { mockUserManagementPort.getUserNotNull(any()) } returns mockUser
        `when`("이전에 열람한 적이 없고, 수신자 이름과 같다면") {
            val response = letterCommandService.verify(verifyCommand)
            then("편지 코드가 검증되고, 편지 ID가 반환되어야 한다") {
                response.letterId shouldNotBeNull {
                    this.isNotBlank()
                    this.isNotEmpty()
                }
                verify { mockSendLetterManagementPort.expireLetter(mockUser.id, sendLetter.id) }
            }
        }

        every { mockSendLetterManagementPort.getLetterByCodeNotNull(any()) } throws LetterException.SendLetterNotFoundException()
        `when`("코드가 존재하지 않는다면") {
            then("예외가 발생해야 한다") {
                shouldThrow<LetterException.SendLetterNotFoundException> {
                    letterCommandService.verify(verifyCommand)
                }
            }
        }

        val anotherUser = User(
            id = DomainId("user-id"),
            username = "another-name",
            profileImage = "profile-image",
            permission = UserPermission(true, true, true),
            birthday = LocalDate.now()
        )
        every { mockSendLetterManagementPort.getLetterByCodeNotNull(any()) } returns sendLetter
        every { mockUserManagementPort.getUserNotNull(any()) } returns anotherUser
        `when`("편지의 수신자 이름과 사용자 이름이 다르면") {
            then("예외가 발생해야 한다") {
                shouldThrow<LetterException.InvalidLetterAccessException> {
                    letterCommandService.verify(verifyCommand)
                }
            }
        }

        every { mockSendLetterManagementPort.verifiedLetter(any(), letterCode) } returns true
        every { mockSendLetterManagementPort.getExpiredLetterNotNull(any(), letterCode) } returns sendLetter
        `when`("이전에 열람한 적이 있는 사용자라면") {
            val response = letterCommandService.verify(verifyCommand)
            then("편지 코드가 검증되고, 편지 ID가 반환되어야 한다") {
                response.letterId shouldNotBeNull {
                    this.isNotBlank()
                    this.isNotEmpty()
                }
            }
        }
    }


    given("검증된 편지를 추가할 때") {
        val letterId = "letter-id"
        val command = AddLetterUsecase.Command.VerifyLetter(letterId, "user-id")
        val sendLetter = SendLetter(
            id = DomainId(letterId),
            receiverName = "receiver-name",
            content = LetterContent(
                "content",
                images = emptyList(),
                templateType = 1
            ),
            senderId = DomainId("sender-id"),
            letterCode = "letter-code"
        )
        every {
            mockSendLetterManagementPort.getExpiredLetterNotNull(
                any(), any(DomainId::class)
            )
        } returns sendLetter
        `when`("해당 편지가 아직 받지 않은 상태라면") {
            letterCommandService.addVerifiedLetter(command)
            then("독립 편지로 저장된다.") {
                verify {
                    mockIndependentLetterManagementPort.save(any())
                }
                verify { mockSendLetterManagementPort.remove(sendLetter.id) }
            }
        }
    }


    given("실물 편지를 추가할 때") {
        val command = AddLetterUsecase.Command.AddPhysicalLetter(
            senderName = "sender-name",
            content = "content",
            images = emptyList(),
            templateType = 1,
            userId = "user-id"
        )
        `when`("편지를 추가하면") {
            letterCommandService.addPhysicalLetter(command)
            then("편지가 저장되어야 한다") {
                verify { mockSendLetterManagementPort.save(any()) }
            }
        }
    }

    given("행성으로 편지를 이동할 때") {
        val command = MoveLetterUsecase.Command.ToSpace(
            letterId = "letter-id",
            userId = "user-id",
            spaceId = "space-id"
        )
        val independentLetter = IndependentLetter(
            id = DomainId("letter-id"),
            content = LetterContent(
                "content",
                images = emptyList(),
                templateType = 1
            ),
            sender = SenderInfo(
                senderName = "sender-name"
            ),
            receiver = ReceiverInfo(
                receiverId = DomainId("user-id")
            ),
            receiveDate = LocalDate.now(),
            isNew = true
        )
        every {
            mockIndependentLetterManagementPort.getIndependentLetterByIdNotNull(DomainId("letter-id"))
        } returns independentLetter
        `when`("편지를 이동하면") {
            letterCommandService.moveToSpace(command)
            then("편지가 이동되어야 한다") {
                verify {
                    mockSpaceLetterManagementPort.saveByIndependentLetter(
                        independentLetter,
                        DomainId("space-id"),
                        DomainId("user-id")
                    )
                }
            }
        }
    }


    given("무소속 편지로 이동할 때") {
        val command = MoveLetterUsecase.Command.ToIndependent(
            letterId = "letter-id",
            userId = "user-id"
        )
        val spaceLetter = SpaceLetter(
            id = DomainId("letter-id"),
            content = LetterContent(
                "content",
                images = emptyList(),
                templateType = 1
            ),
            sender = SenderInfo(
                senderName = "sender-name"
            ),
            receiver = ReceiverInfo(
                receiverId = DomainId("user-id")
            ),
            receiveDate = LocalDate.now(),
            spaceId = DomainId("space-id")
        )
        every {
            mockSpaceLetterManagementPort.getSpaceLetterNotNull(
                DomainId("letter-id"),
                DomainId("user-id")
            )
        } returns spaceLetter
        `when`("편지를 이동하면") {
            letterCommandService.moveToIndependent(command)
            then("편지가 이동되어야 한다") {
                verify { mockIndependentLetterManagementPort.saveBySpaceLetter(spaceLetter, DomainId("user-id")) }
            }
        }
    }


    given("행성 내의 편지 삭제 요청이 들어올 떄") {
        val command = RemoveLetterUsecase.Command.SpaceLetter(
            letterId = "letter-id",
            userId = "user-id"
        )
        val spaceLetter = SpaceLetter(
            id = DomainId("letter-id"),
            content = LetterContent(
                "content",
                images = emptyList(),
                templateType = 1
            ),
            sender = SenderInfo(
                senderName = "sender-name"
            ),
            receiver = ReceiverInfo(
                receiverId = DomainId("user-id")
            ),
            receiveDate = LocalDate.now(),
            spaceId = DomainId("space-id")
        )
        every {
            mockSpaceLetterManagementPort.getSpaceLetterNotNull(
                DomainId("letter-id"),
                DomainId("user-id")
            )
        } returns spaceLetter
        `when`("편지를 삭제하면") {
            letterCommandService.removeSpaceLetter(command)
            then("편지가 삭제되어야 한다") {
                verify { mockSpaceLetterManagementPort.delete(spaceLetter) }
            }
        }

        clearMocks(mockSpaceLetterManagementPort)
        every {
            mockSpaceLetterManagementPort.getSpaceLetterNotNull(
                DomainId("letter-id"),
                DomainId("user-id")
            )
        } throws LetterException.ReceiveLetterNotFoundException()
        `when`("편지가 존재하지 않으면") {
            then("예외가 발생해야 한다") {
                shouldThrow<LetterException.ReceiveLetterNotFoundException> {
                    letterCommandService.removeSpaceLetter(command)
                }.apply {
                    verify(exactly = 0) {
                        mockSpaceLetterManagementPort.delete(any())
                    }
                }

            }
        }
    }


}) {
}