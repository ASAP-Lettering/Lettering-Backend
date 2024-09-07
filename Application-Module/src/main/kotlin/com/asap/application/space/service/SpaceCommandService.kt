package com.asap.application.space.service

import com.asap.application.space.port.`in`.SpaceCreateUsecase
import com.asap.application.space.port.`in`.SpaceUpdateNameUsecase
import com.asap.application.space.port.out.SpaceManagementPort
import com.asap.domain.common.DomainId
import org.springframework.stereotype.Service

@Service
class SpaceCommandService(
    private val spaceManagementPort: SpaceManagementPort,
) : SpaceCreateUsecase, SpaceUpdateNameUsecase {
    override fun create(command: SpaceCreateUsecase.Command) {
        spaceManagementPort.createSpace(
            userId = DomainId(command.userId),
            spaceName = command.spaceName,
            templateType = command.templateType
        )
    }

    override fun update(command: SpaceUpdateNameUsecase.Command) {
        val space = spaceManagementPort.getSpace(
            userId = DomainId(command.userId),
            spaceId = DomainId(command.spaceId)
        )
        val updatedSpace = space.updateName(command.name)
        spaceManagementPort.update(updatedSpace)
    }
}