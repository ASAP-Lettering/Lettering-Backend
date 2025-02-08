package com.asap.application.space.service

import com.asap.application.space.exception.SpaceException
import com.asap.application.space.port.`in`.CreateSpaceUsecase
import com.asap.application.space.port.`in`.DeleteSpaceUsecase
import com.asap.application.space.port.`in`.UpdateSpaceNameUsecase
import com.asap.application.space.port.`in`.UpdateSpaceUsecase
import com.asap.application.space.port.out.SpaceManagementPort
import com.asap.domain.SpaceFixture
import com.asap.domain.common.DomainId
import com.asap.domain.space.entity.Space
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class SpaceCommandServiceTest :
    BehaviorSpec({

        val spaceManagementPort = mockk<SpaceManagementPort>(relaxed = true)

        val spaceCommandService =
            SpaceCommandService(
                spaceManagementPort,
            )

        given("스페이스 생성 요청이 들어왔을 때") {
            val spaceCreateCommand =
                CreateSpaceUsecase.Command(
                    userId = "userId",
                    spaceName = "spaceName",
                    templateType = 1,
                )
            `when`("유저 아이디, 스페이스 이름, 템플릿 타입이 주어진다면") {
                spaceCommandService.create(spaceCreateCommand)
                then("스페이스를 생성한다") {
                    verify {
                        spaceManagementPort.save(any(Space::class))
                    }
                }
            }
        }

        given("스페이스 이름 변경 요청이 들어왔을 때") {
            val spaceUpdateNameCommand =
                UpdateSpaceNameUsecase.Command(
                    userId = "userId",
                    spaceId = "spaceId",
                    name = "newName",
                )
            val mockSpace = SpaceFixture.createSpace(
                id = DomainId(spaceUpdateNameCommand.spaceId),
                userId = DomainId(spaceUpdateNameCommand.userId),
                name = "oldName",
                templateType = 1,
            )

            every {
                spaceManagementPort.getSpaceNotNull(
                    userId = DomainId(spaceUpdateNameCommand.userId),
                    spaceId = DomainId(spaceUpdateNameCommand.spaceId),
                )
            } returns mockSpace
            `when`("유저 아이디, 스페이스 아이디, 새로운 이름이 주어진다면") {
                spaceCommandService.update(spaceUpdateNameCommand)
                then("스페이스 이름을 변경한다") {
                    verify {
                        spaceManagementPort.getSpaceNotNull(
                            userId = DomainId(spaceUpdateNameCommand.userId),
                            spaceId = DomainId(spaceUpdateNameCommand.spaceId),
                        )
                    }
                    verify {
                        spaceManagementPort.update(mockSpace)
                    }
                }
            }
        }

        given("스페이스 삭제 요청이 들어왔을 때") {
            val spaceDeleteOneCommand =
                DeleteSpaceUsecase.DeleteOneCommand(
                    userId = "userId",
                    spaceId = "spaceId",
                )
            `when`("유저 아이디, 스페이스 아이디가 주어진다면") {
                spaceCommandService.deleteOne(spaceDeleteOneCommand)
                then("스페이스를 삭제한다") {
                    verify {
                        spaceManagementPort.deleteBy(
                            any(Space::class),
                        )
                    }
                }
            }

            val spaceDeleteAllCommand =
                DeleteSpaceUsecase.DeleteAllCommand(
                    userId = "userId",
                    spaceIds = listOf("spaceId1", "spaceId2"),
                )
            `when`("여러 스페이스 아이디가 주어진다면") {
                spaceCommandService.deleteAllBy(spaceDeleteAllCommand)
                then("여러 스페이스를 삭제한다") {
                    verify {
                        spaceManagementPort.deleteBy(
                            any(Space::class),
                        )
                    }
                }
            }
        }

        given("스페이스 인덱스 수정 요청이 들어올 때") {
            val spaceUpdateIndexCommand =
                UpdateSpaceUsecase.Command.Index(
                    userId = "userId",
                    orders =
                        listOf(
                            UpdateSpaceUsecase.Command.SpaceOrder("spaceId1", 1),
                            UpdateSpaceUsecase.Command.SpaceOrder("spaceId2", 0),
                        ),
                )
            val spaces =
                listOf(
                    SpaceFixture.createSpace(
                        id = DomainId(spaceUpdateIndexCommand.orders[0].spaceId),
                        userId = DomainId(spaceUpdateIndexCommand.userId),
                    ),
                    SpaceFixture.createSpace(
                        id = DomainId(spaceUpdateIndexCommand.orders[1].spaceId),
                        userId = DomainId(spaceUpdateIndexCommand.userId),
                    ),
                )
            every { spaceManagementPort.getAllSpaceBy(DomainId(spaceUpdateIndexCommand.userId)) } returns spaces
            `when`("유저 아이디, 스페이스 순서가 주어진다면") {
                spaceCommandService.update(spaceUpdateIndexCommand)
                then("스페이스 순서를 수정한다") {
                    verify {
                        spaceManagementPort.saveAll(spaces)
                    }
                }
            }

            val invalidCommand =
                UpdateSpaceUsecase.Command.Index(
                    userId = "userId",
                    orders =
                        listOf(
                            UpdateSpaceUsecase.Command.SpaceOrder("spaceId1", 1),
                            UpdateSpaceUsecase.Command.SpaceOrder("spaceId2", 2),
                            UpdateSpaceUsecase.Command.SpaceOrder("spaceId3", 3),
                        ),
                )
            every {
                spaceManagementPort.getAllSpaceBy(DomainId(invalidCommand.userId))
            } returns spaces
            `when`("인덱스 검증과정에서 예외가 발생한다면") {
                then("스페이스 순서를 수정하지 않는다") {
                    shouldThrow<SpaceException.InvalidSpaceUpdateException> {
                        spaceCommandService.update(
                            invalidCommand,
                        )
                    }
                }
            }
        }
    })
