package com.asap.application.space.service

import com.asap.application.space.port.`in`.SpaceCreateUsecase
import com.asap.application.space.port.out.SpaceManagementPort
import com.asap.domain.common.DomainId
import org.springframework.stereotype.Service

@Service
class SpaceCommandService(
    private val spaceManagementPort: SpaceManagementPort,
) : SpaceCreateUsecase {
    override fun create(command: SpaceCreateUsecase.Command) {
        spaceManagementPort.createSpace(
            userId = DomainId(command.userId),
            spaceName = command.spaceName,
            templateType = command.templateType
        )
    }
}