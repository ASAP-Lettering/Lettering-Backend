package com.asap.application.letter.service

import com.asap.application.letter.exception.LetterException
import com.asap.application.letter.port.`in`.*
import com.asap.application.letter.port.out.IndependentLetterManagementPort
import com.asap.application.letter.port.out.SendLetterManagementPort
import com.asap.application.letter.port.out.SpaceLetterManagementPort
import com.asap.application.user.port.out.UserManagementPort
import com.asap.domain.LetterFixture
import com.asap.domain.UserFixture
import com.asap.domain.common.DomainId
import com.asap.domain.letter.entity.SendLetter
import com.asap.domain.letter.vo.LetterContent
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class LetterCommandServiceTest :
    BehaviorSpec({

        val mockSendLetterManagementPort = mockk<SendLetterManagementPort>(relaxed = true)
        val mockIndependentLetterManagementPort = mockk<IndependentLetterManagementPort>(relaxed = true)
        val mockUserManagementPort = mockk<UserManagementPort>(relaxed = true)
        val mockSpaceLetterManagementPort = mockk<SpaceLetterManagementPort>(relaxed = true)

        val letterCommandService =
            LetterCommandService(
                mockSendLetterManagementPort,
                mockIndependentLetterManagementPort,
                mockSpaceLetterManagementPort,
                mockUserManagementPort,
            )

        given("편지 전송 요청이 들어올 때") {
            val command =
                SendLetterUsecase.Command(
                    userId = "user-id",
                    receiverName = "receiver-name",
                    content = "content",
                    images = emptyList(),
                    templateType = 1,
                    draftId = null,
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

        given("익명 편지 전송 요청이 들어올 때") {
            val command =
                SendLetterUsecase.AnonymousCommand(
                    receiverName = "receiver-name",
                    content = "content",
                    images = emptyList(),
                    templateType = 1,
                )
            `when`("익명 편지 전송 요청을 처리하면") {
                val response = letterCommandService.sendAnonymous(command)
                then("편지 코드가 생성되고, 편지가 저장되어야 한다") {
                    response.letterCode shouldNotBeNull {
                        this.isNotBlank()
                        this.isNotEmpty()
                    }
                    verify { mockSendLetterManagementPort.save(any()) }
                }
            }
            
            val commandWithSenderName =
                SendLetterUsecase.AnonymousCommand(
                    senderName = "Test Sender",
                    receiverName = "receiver-name",
                    content = "content",
                    images = emptyList(),
                    templateType = 1,
                )
            `when`("발송자 이름이 제공된 익명 편지 전송 요청을 처리하면") {
                val response = letterCommandService.sendAnonymous(commandWithSenderName)
                then("편지 코드가 생성되고, 제공된 발송자 이름으로 편지가 저장되어야 한다") {
                    response.letterCode shouldNotBeNull {
                        this.isNotBlank()
                        this.isNotEmpty()
                    }
                    verify { 
                        mockSendLetterManagementPort.save(match { sendLetter ->
                            sendLetter.senderName == "Test Sender"
                        })
                    }
                }
            }
            
            val commandWithNullSenderName =
                SendLetterUsecase.AnonymousCommand(
                    senderName = null,
                    receiverName = "receiver-name",
                    content = "content",
                    images = emptyList(),
                    templateType = 1,
                )
            `when`("발송자 이름이 null인 익명 편지 전송 요청을 처리하면") {
                val response = letterCommandService.sendAnonymous(commandWithNullSenderName)
                then("편지 코드가 생성되고, Anonymous로 편지가 저장되어야 한다") {
                    response.letterCode shouldNotBeNull {
                        this.isNotBlank()
                        this.isNotEmpty()
                    }
                    verify { 
                        mockSendLetterManagementPort.save(match { sendLetter ->
                            sendLetter.senderName == "Anonymous"
                        })
                    }
                }
            }
        }

        given("편지 검증 시에") {
            val letterCode = "letter-code"
            val mockUser = UserFixture.createUser(username = "receiver-name")
            val verifyCommand =
                VerifyLetterAccessibleUsecase.Command(
                    letterCode = letterCode,
                    userId = mockUser.id.value,
                )
            val sendLetter =
                SendLetter.create(
                    receiverName = "receiver-name",
                    content =
                        LetterContent(
                            "content",
                            images = mutableListOf(),
                            templateType = 1,
                        ),
                    senderId = DomainId("sender-id"),
                    letterCode = letterCode,
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

            val anotherUser = UserFixture.createUser()
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
            every { mockSendLetterManagementPort.getReadLetterNotNull(any(), letterCode) } returns sendLetter
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
            val userId = UserFixture.createUser()
            val command = AddLetterUsecase.Command.VerifyLetter(letterId, userId.id.value)
            val sendLetter = LetterFixture.generateSendLetter(senderId = userId.id)
            every {
                mockSendLetterManagementPort.getReadLetterNotNull(
                    any(),
                    any(DomainId::class),
                )
            } returns sendLetter
            `when`("해당 편지가 아직 받지 않은 상태라면") {
                letterCommandService.addVerifiedLetter(command)
                then("독립 편지로 저장된다.") {
                    verify {
                        mockIndependentLetterManagementPort.save(any())
                    }
                }
            }
        }

        given("실물 편지를 추가할 때") {
            val command =
                AddLetterUsecase.Command.AddPhysicalLetter(
                    senderName = "sender-name",
                    content = "content",
                    images = emptyList(),
                    templateType = 1,
                    userId = "user-id",
                    draftId = null,
                )
            `when`("편지를 추가하면") {
                letterCommandService.addPhysicalLetter(command)
                then("편지가 저장되어야 한다") {
                    verify { mockSendLetterManagementPort.save(any()) }
                }
            }
        }

        given("행성으로 편지를 이동할 때") {
            val command =
                MoveLetterUsecase.Command.ToSpace(
                    letterId = "letter-id",
                    userId = "user-id",
                    spaceId = "space-id",
                )
            val independentLetter =
                LetterFixture.generateIndependentLetter(
                    id = DomainId("letter-id"),
                    senderId = DomainId("sender-id"),
                    receiverId = DomainId("user-id"),
                )
            every {
                mockIndependentLetterManagementPort.getIndependentLetterByIdNotNull(DomainId("letter-id"))
            } returns independentLetter
            `when`("편지를 이동하면") {
                letterCommandService.moveToSpace(command)
                then("편지가 이동되어야 한다") {
                    verify {
                        mockSpaceLetterManagementPort.save(any())
                    }
                }
            }
        }

        given("무소속 편지로 이동할 때") {
            val command =
                MoveLetterUsecase.Command.ToIndependent(
                    letterId = "letter-id",
                    userId = "user-id",
                )
            val spaceLetter =
                LetterFixture.generateSpaceLetter(
                    id = DomainId("letter-id"),
                    senderId = DomainId("sender-id"),
                    receiverId = DomainId("user-id"),
                    spaceId = DomainId("space-id"),
                )
            every {
                mockSpaceLetterManagementPort.getSpaceLetterNotNull(
                    DomainId("letter-id"),
                    DomainId("user-id"),
                )
            } returns spaceLetter
            `when`("편지를 이동하면") {
                letterCommandService.moveToIndependent(command)
                then("편지가 이동되어야 한다") {
                    verify {
                        mockIndependentLetterManagementPort.save(any())
                    }
                }
            }
        }

        given("행성 내의 편지 삭제 요청이 들어올 떄") {
            val command =
                RemoveLetterUsecase.Command.SpaceLetter(
                    letterId = "letter-id",
                    userId = "user-id",
                )
            val spaceLetter =
                LetterFixture.generateSpaceLetter(
                    id = DomainId("letter-id"),
                    senderId = DomainId("sender-id"),
                    receiverId = DomainId("user-id"),
                    spaceId = DomainId("space-id"),
                )
            every {
                mockSpaceLetterManagementPort.getSpaceLetterNotNull(
                    DomainId("letter-id"),
                    DomainId("user-id"),
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
                    DomainId("user-id"),
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

        given("궤도 편지 삭제 요청이 들어올 떄") {
            val command = RemoveLetterUsecase.Command.IndependentLetter("letter-id", "user-id")
            val independentLetter =
                LetterFixture.generateIndependentLetter(
                    id = DomainId("letter-id"),
                    senderId = DomainId("sender-id"),
                    receiverId = DomainId("user-id"),
                )
            every { mockIndependentLetterManagementPort.getIndependentLetterByIdNotNull(DomainId("letter-id")) } returns independentLetter
            `when`("편지를 삭제하면") {
                letterCommandService.removeIndependentLetter(command)
                then("편지가 삭제되어야 한다") {
                    verify { mockIndependentLetterManagementPort.delete(independentLetter) }
                }
            }

            clearMocks(mockIndependentLetterManagementPort)
            every { mockIndependentLetterManagementPort.getIndependentLetterByIdNotNull(DomainId("letter-id")) } throws
                LetterException.ReceiveLetterNotFoundException()
            `when`("편지가 존재하지 않으면") {
                then("예외가 발생해야 한다") {
                    shouldThrow<LetterException.ReceiveLetterNotFoundException> {
                        letterCommandService.removeIndependentLetter(command)
                    }.apply {
                        verify(exactly = 0) { mockIndependentLetterManagementPort.delete(any()) }
                    }
                }
            }
        }

        given("궤도 편지 수정 요청이 들어올 떄") {
            val command =
                UpdateLetterUsecase.Command.Independent(
                    letterId = "letter-id",
                    senderName = "sender-name",
                    content = "content",
                    images = emptyList(),
                    userId = "user-id",
                    templateType = 1,
                )
            val independentLetter =
                LetterFixture.generateIndependentLetter(
                    id = DomainId("letter-id"),
                    senderId = DomainId("sender-id"),
                    receiverId = DomainId("user-id"),
                )
            every { mockIndependentLetterManagementPort.getIndependentLetterByIdNotNull(DomainId("letter-id")) } returns independentLetter
            `when`("편지를 수정하면") {
                letterCommandService.updateIndependentLetter(command)
                then("편지가 수정되어야 한다") {
                    verify { mockIndependentLetterManagementPort.save(independentLetter) }
                }
            }
        }

        given("행성 편지 수정 요청이 들어올 떄") {
            val command = UpdateLetterUsecase.Command.Space("letter-id", "user-id", "name", "content", emptyList(), 1)
            val spaceLetter =
                LetterFixture.generateSpaceLetter(
                    id = DomainId("letter-id"),
                    senderId = DomainId("sender-id"),
                    receiverId = DomainId("user-id"),
                    spaceId = DomainId("space-id"),
                )
            every {
                mockSpaceLetterManagementPort.getSpaceLetterNotNull(
                    DomainId("letter-id"),
                    DomainId("user-id"),
                )
            } returns spaceLetter
            `when`("편지를 수정하면") {
                letterCommandService.updateSpaceLetter(command)
                then("편지가 수정되어야 한다") {
                    verify { mockSpaceLetterManagementPort.save(spaceLetter) }
                }
            }
        }


        given("익명 편지를 사용자의 편지로 추가할 때") {
            val letterCode = "letter-code"
            val userId = "user-id"
            val command = AddLetterUsecase.Command.AddAnonymousLetter(
                letterCode = letterCode,
                userId = userId,
            )

            // Create an anonymous letter using SendLetter.createAnonymous
            val content = LetterContent(
                content = "content",
                templateType = 1,
                images = mutableListOf("image1", "image2"),
            )
            val sendLetter = SendLetter.createAnonymous(
                content = content,
                receiverName = "receiverName",
                letterCode = letterCode,
                senderName = "Anonymous",
            )

            every { 
                mockSendLetterManagementPort.getLetterByCodeNotNull(letterCode) 
            } returns sendLetter

            `when`("익명 편지를 사용자의 편지로 추가하면") {
                letterCommandService.addAnonymousLetter(command)

                then("편지의 발신자 ID가 설정되고 저장되어야 한다") {
                    verify { mockSendLetterManagementPort.save(sendLetter) }
                }
            }
        }

        given("보낸 편지 삭제 요청이 들어올 때") {
            val userId = "user-id"
            val sendLetters =
                (0..3).map {
                    LetterFixture.generateSendLetter()
                }
            `when`("하나의 편지만 삭제하면") {
                val command = RemoveLetterUsecase.Command.SendLetter(sendLetters[0].id.value, userId)
                every {
                    mockSendLetterManagementPort.getSendLetterBy(
                        DomainId(command.letterId),
                        DomainId(command.userId),
                    )
                } returns sendLetters[0]
                letterCommandService.removeSenderLetterBy(command)
                then("편지가 삭제되어야 한다") {
                    verify { mockSendLetterManagementPort.delete(any()) }
                    verify { mockSendLetterManagementPort.save(any()) }
                }
            }

            `when`("여러 편지를 삭제하면") {
                val ids = sendLetters.map { it.id }
                val command = RemoveLetterUsecase.Command.SendLetters(ids.map { it.value }, userId)
                every { mockSendLetterManagementPort.getAllBy(DomainId(userId), ids) } returns sendLetters
                letterCommandService.removeAllSenderLetterBy(command)
                then("편지가 삭제되어야 한다") {
                    verify { mockSendLetterManagementPort.delete(any()) }
                    verify { mockSendLetterManagementPort.save(any()) }
                }
            }
        }
    })
