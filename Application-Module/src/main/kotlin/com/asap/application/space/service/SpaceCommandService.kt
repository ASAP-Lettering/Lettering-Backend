package com.asap.application.space.service

import com.asap.application.space.exception.SpaceException
import com.asap.application.space.port.`in`.CreateSpaceUsecase
import com.asap.application.space.port.`in`.DeleteSpaceUsecase
import com.asap.application.space.port.`in`.UpdateSpaceNameUsecase
import com.asap.application.space.port.`in`.UpdateSpaceUsecase
import com.asap.application.space.port.out.SpaceManagementPort
import com.asap.common.exception.DefaultException
import com.asap.domain.common.DomainId
import com.asap.domain.space.entity.Space
import com.asap.domain.space.service.SpaceIndexValidator
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SpaceCommandService(
    private val spaceManagementPort: SpaceManagementPort,
) : CreateSpaceUsecase,
    UpdateSpaceNameUsecase,
    DeleteSpaceUsecase,
    UpdateSpaceUsecase {
    private val spaceIndexValidator: SpaceIndexValidator = SpaceIndexValidator()

    override fun create(command: CreateSpaceUsecase.Command) {
        val userId = DomainId(command.userId)
        Space.create(
            id = DomainId.generate(),
            userId = userId,
            name = command.spaceName,
            templateType = command.templateType,
        ).apply {
            spaceManagementPort.save(this)
        }

        // TODO 동시성 문제가 발생한다면?
        if (spaceManagementPort.countByUserId(userId) == 1L) {
            updateMainSpace(userId)
        }

        reIndexingSpaceOrder(userId)
    }

    override fun deleteOne(command: DeleteSpaceUsecase.DeleteOneCommand) {
        val userId = DomainId(command.userId)
        spaceManagementPort
            .getSpaceNotNull(
                userId = userId,
                spaceId = DomainId(command.spaceId),
            ).apply {
                if (isMain) {
                    updateMainSpace(userId)
                }

                delete()
                spaceManagementPort.deleteBy(this)
            }
        reIndexingSpaceOrder(userId)
    }

    override fun deleteAllBy(command: DeleteSpaceUsecase.DeleteAllCommand) {
        val userId = DomainId(command.userId)
        spaceManagementPort
            .getAllSpaceBy(
                userId = userId,
                spaceIds = command.spaceIds.map { DomainId(it) },
            ).forEach {
                if (it.isMain) {
                    updateMainSpace(userId)
                }
                it.delete()
                spaceManagementPort.deleteBy(it)
            }

        reIndexingSpaceOrder(userId)
    }

    override fun deleteAllBy(command: DeleteSpaceUsecase.DeleteAllUser) {
        spaceManagementPort.getAllSpaceBy(DomainId(command.userId)).forEach {
            it.delete()
            spaceManagementPort.deleteBy(it)
        }
    }

    override fun update(command: UpdateSpaceUsecase.Command.Index) {
        val spaces = spaceManagementPort.getAllSpaceBy(DomainId(command.userId))
        val changeIndexMap = command.orders.associateBy({ DomainId(it.spaceId) }, { it.index })

        try {
            spaceIndexValidator.validate(
                spaces = spaces,
                validateIndex = changeIndexMap,
            )
        } catch (e: DefaultException.InvalidArgumentException) {
            throw SpaceException.InvalidSpaceUpdateException(message = e.message)
        }

        spaces.map {
            it.updateIndex(changeIndexMap.getValue(it.id))
        }
        spaceManagementPort.saveAll(spaces)
    }

    override fun update(command: UpdateSpaceUsecase.Command.Main) {
        val spaces = spaceManagementPort.getAllSpaceBy(
            userId = DomainId(command.userId),
        ).onEach {
            if (it.id.value == command.spaceId) {
                it.updateToMain()
            } else {
                it.updateToSub()
            }
        }

        spaceManagementPort.saveAll(spaces)
    }

    override fun update(command: UpdateSpaceNameUsecase.Command) {
        val space =
            spaceManagementPort.getSpaceNotNull(
                userId = DomainId(command.userId),
                spaceId = DomainId(command.spaceId),
            )

        space.updateName(command.name)
        spaceManagementPort.update(space)
    }

    private fun reIndexingSpaceOrder(userId: DomainId) {
        val spaces = spaceManagementPort
            .getAllSpaceBy(userId)
            .sortedBy { it.index }
            .onEachIndexed { index, space ->
                space.updateIndex(index)
            }
        spaceManagementPort.saveAll(spaces)
    }


    private fun updateMainSpace(userId: DomainId) {
        val spaces = spaceManagementPort.getAllSpaceBy(userId)

        if (spaces.isEmpty()) {
            return
        }

        spaces.forEach { it.updateToSub() }
        spaces.first().updateToMain()

        spaceManagementPort.saveAll(spaces)
    }
}
