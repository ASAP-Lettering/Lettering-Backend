package com.asap.application.space.service

import com.asap.application.space.port.`in`.SpaceCreateUsecase
import com.asap.application.space.port.`in`.SpaceUpdateNameUsecase
import com.asap.application.space.port.out.SpaceManagementPort
import com.asap.domain.common.DomainId
import com.asap.domain.space.entity.Space
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class SpaceCommandServiceTest:BehaviorSpec({

    val spaceManagementPort = mockk<SpaceManagementPort>(relaxed = true)

    val spaceCommandService = SpaceCommandService(
        spaceManagementPort
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
            name = "oldName"
        )
        every { spaceManagementPort.getSpace(
            userId = DomainId(spaceUpdateNameCommand.userId),
            spaceId = DomainId(spaceUpdateNameCommand.spaceId)
        ) } returns mockSpace
        `when`("유저 아이디, 스페이스 아이디, 새로운 이름이 주어진다면") {
            spaceCommandService.update(spaceUpdateNameCommand)
            then("스페이스 이름을 변경한다") {
                verify {
                    spaceManagementPort.getSpace(
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
}) {
}