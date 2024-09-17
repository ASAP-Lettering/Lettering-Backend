package com.asap.application.space.service

import com.asap.application.space.exception.SpaceException
import com.asap.application.space.port.`in`.SpaceCreateUsecase
import com.asap.application.space.port.`in`.SpaceDeleteUsecase
import com.asap.application.space.port.`in`.SpaceUpdateIndexUsecase
import com.asap.application.space.port.`in`.SpaceUpdateNameUsecase
import com.asap.application.space.port.out.SpaceManagementPort
import com.asap.domain.common.DomainId
import com.asap.domain.space.entity.IndexedSpace
import com.asap.domain.space.entity.Space
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class SpaceCommandServiceTest : BehaviorSpec({

    val spaceManagementPort = mockk<SpaceManagementPort>(relaxed = true)

    val spaceCommandService = SpaceCommandService(
        spaceManagementPort,
    )


    given("스페이스 생성 요청이 들어왔을 때") {
        val spaceCreateCommand = SpaceCreateUsecase.Command(
            userId = "userId",
            spaceName = "spaceName",
            templateType = 1
        )
        `when`("유저 아이디, 스페이스 이름, 템플릿 타입이 주어진다면") {
            spaceCommandService.create(spaceCreateCommand)
            then("스페이스를 생성한다") {
                verify {
                    spaceManagementPort.createSpace(
                        userId = DomainId(spaceCreateCommand.userId),
                        spaceName = spaceCreateCommand.spaceName,
                        templateType = spaceCreateCommand.templateType
                    )
                }
            }
        }
    }

    given("스페이스 이름 변경 요청이 들어왔을 때") {
        val spaceUpdateNameCommand = SpaceUpdateNameUsecase.Command(
            userId = "userId",
            spaceId = "spaceId",
            name = "newName"
        )
        val mockSpace = Space(
            id = DomainId(spaceUpdateNameCommand.spaceId),
            userId = DomainId(spaceUpdateNameCommand.userId),
            name = "oldName",
            templateType = 1
        )
        every {
            spaceManagementPort.getSpaceNotNull(
                userId = DomainId(spaceUpdateNameCommand.userId),
                spaceId = DomainId(spaceUpdateNameCommand.spaceId)
            )
        } returns mockSpace
        `when`("유저 아이디, 스페이스 아이디, 새로운 이름이 주어진다면") {
            spaceCommandService.update(spaceUpdateNameCommand)
            then("스페이스 이름을 변경한다") {
                verify {
                    spaceManagementPort.getSpaceNotNull(
                        userId = DomainId(spaceUpdateNameCommand.userId),
                        spaceId = DomainId(spaceUpdateNameCommand.spaceId)
                    )
                }
                verify {
                    spaceManagementPort.update(mockSpace.updateName(spaceUpdateNameCommand.name))
                }
            }
        }
    }

    given("스페이스 삭제 요청이 들어왔을 때") {
        val spaceDeleteOneCommand = SpaceDeleteUsecase.DeleteOneCommand(
            userId = "userId",
            spaceId = "spaceId"
        )
        `when`("유저 아이디, 스페이스 아이디가 주어진다면") {
            spaceCommandService.deleteOne(spaceDeleteOneCommand)
            then("스페이스를 삭제한다") {
                verify {
                    spaceManagementPort.deleteById(
                        userId = DomainId(spaceDeleteOneCommand.userId),
                        spaceId = DomainId(spaceDeleteOneCommand.spaceId)
                    )
                }
            }
        }

        val spaceDeleteAllCommand = SpaceDeleteUsecase.DeleteAllCommand(
            userId = "userId",
            spaceIds = listOf("spaceId1", "spaceId2")
        )
        `when`("여러 스페이스 아이디가 주어진다면") {
            spaceCommandService.deleteAll(spaceDeleteAllCommand)
            then("여러 스페이스를 삭제한다") {
                verify {
                    spaceManagementPort.deleteAllBySpaceIds(
                        userId = DomainId(spaceDeleteAllCommand.userId),
                        spaceIds = spaceDeleteAllCommand.spaceIds.map { DomainId(it) }
                    )
                }
            }
        }
    }

    given("스페이스 인덱스 수정 요청이 들어올 때") {
        val spaceUpdateIndexCommand = SpaceUpdateIndexUsecase.Command(
            userId = "userId",
            orders = listOf(
                SpaceUpdateIndexUsecase.Command.SpaceOrder("spaceId1", 1),
                SpaceUpdateIndexUsecase.Command.SpaceOrder("spaceId2", 0)
            )
        )
        val indexedSpaces = listOf(
            IndexedSpace(
                id = DomainId("spaceId1"),
                userId = DomainId("userId"),
                name = "space1",
                index = 0,
                templateType = 1
            ),
            IndexedSpace(
                id = DomainId("spaceId2"),
                userId = DomainId("userId"),
                name = "space2",
                index = 1,
                templateType = 1
            )
        )
        every { spaceManagementPort.getAllIndexedSpace(DomainId(spaceUpdateIndexCommand.userId)) } returns indexedSpaces
        `when`("유저 아이디, 스페이스 순서가 주어진다면") {
            spaceCommandService.update(spaceUpdateIndexCommand)
            then("스페이스 순서를 수정한다") {
                verify {
                    spaceManagementPort.updateIndexes(
                        userId = DomainId(spaceUpdateIndexCommand.userId),
                        orders = listOf(
                            indexedSpaces[0].updateIndex(1),
                            indexedSpaces[1].updateIndex(0)
                        )
                    )
                }
            }
        }


        val invalidCommand = SpaceUpdateIndexUsecase.Command(
            userId = "userId",
            orders = listOf(
                SpaceUpdateIndexUsecase.Command.SpaceOrder("spaceId1", 1),
                SpaceUpdateIndexUsecase.Command.SpaceOrder("spaceId2", 2),
                SpaceUpdateIndexUsecase.Command.SpaceOrder("spaceId3", 3)
            )
        )
        every {
            spaceManagementPort.getAllIndexedSpace(DomainId(invalidCommand.userId))
        } returns indexedSpaces
        `when`("인덱스 검증과정에서 예외가 발생한다면") {
            then("스페이스 순서를 수정하지 않는다") {
                shouldThrow<SpaceException.InvalidSpaceUpdateException> {
                    spaceCommandService.update(
                        invalidCommand
                    )
                }
            }
        }
    }


}) {
}