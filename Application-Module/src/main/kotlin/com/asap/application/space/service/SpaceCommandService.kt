package com.asap.application.space.service

import com.asap.application.space.exception.SpaceException
import com.asap.application.space.port.`in`.SpaceCreateUsecase
import com.asap.application.space.port.`in`.SpaceDeleteUsecase
import com.asap.application.space.port.`in`.SpaceUpdateIndexUsecase
import com.asap.application.space.port.`in`.SpaceUpdateNameUsecase
import com.asap.application.space.port.out.SpaceManagementPort
import com.asap.common.exception.DefaultException
import com.asap.domain.common.DomainId
import com.asap.domain.space.service.SpaceIndexValidator
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SpaceCommandService(
    private val spaceManagementPort: SpaceManagementPort,
) : SpaceCreateUsecase,
    SpaceUpdateNameUsecase,
    SpaceDeleteUsecase,
    SpaceUpdateIndexUsecase {
    private val spaceIndexValidator: SpaceIndexValidator = SpaceIndexValidator()

    override fun create(command: SpaceCreateUsecase.Command) {
        spaceManagementPort.createSpace(
            userId = DomainId(command.userId),
            spaceName = command.spaceName,
            templateType = command.templateType,
        )
    }

    override fun update(command: SpaceUpdateNameUsecase.Command) {
        val space =
            spaceManagementPort.getSpaceNotNull(
                userId = DomainId(command.userId),
                spaceId = DomainId(command.spaceId),
            )
        val updatedSpace = space.updateName(command.name)
        spaceManagementPort.update(updatedSpace)
    }

    override fun deleteOne(command: SpaceDeleteUsecase.DeleteOneCommand) {
        spaceManagementPort.deleteById(
            userId = DomainId(command.userId),
            spaceId = DomainId(command.spaceId),
        )
    }

    override fun deleteAll(command: SpaceDeleteUsecase.DeleteAllCommand) {
        spaceManagementPort.deleteAllBySpaceIds(
            userId = DomainId(command.userId),
            spaceIds = command.spaceIds.map { DomainId(it) },
        )
    }

    override fun update(command: SpaceUpdateIndexUsecase.Command) {
        val indexedSpaces = spaceManagementPort.getAllIndexedSpace(DomainId(command.userId))
        val changeIndexMap = command.orders.associateBy({ DomainId(it.spaceId) }, { it.index })

        try {
            spaceIndexValidator.validate(
                indexedSpaces = indexedSpaces,
                validateIndex = changeIndexMap,
            )
        } catch (e: DefaultException.InvalidArgumentException) {
            throw SpaceException.InvalidSpaceUpdateException()
        }

        val updatedSpaces =
            indexedSpaces.map {
                it.updateIndex(changeIndexMap.getValue(it.id))
            }
        spaceManagementPort.updateIndexes(
            userId = DomainId(command.userId),
            orders = updatedSpaces,
        )
    }
}
