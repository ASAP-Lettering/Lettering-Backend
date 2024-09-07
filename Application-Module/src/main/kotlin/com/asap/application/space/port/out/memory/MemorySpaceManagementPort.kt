package com.asap.application.space.port.out.memory

import com.asap.application.space.port.out.SpaceManagementPort
import com.asap.domain.common.DomainId
import com.asap.domain.space.entity.MainSpace
import com.asap.domain.space.entity.Space
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
@Primary
class MemorySpaceManagementPort(

): SpaceManagementPort {

    private val spaces = mutableMapOf<String, SpaceEntity>()

    override fun getMainSpace(
        userId: DomainId
    ): MainSpace {
        val findSpace = spaces.filter {
            it.value.userId == userId.value
        }.values.sortedBy {
            it.createdAt
        }[0]

        return MainSpace(
            id = DomainId(findSpace.id)
        )
    }

    override fun createSpace(
        userId: DomainId,
        spaceName: String,
        templateType: Int
    ): Space {
        val space = Space(
            userId = userId,
            name = spaceName
        )
        val spaceEntity = SpaceEntity(
            id = space.id.value,
            userId = userId.value,
            name = space.name,
            templateType = templateType
        )
        spaces[space.id.value] = spaceEntity
        return space
    }

    override fun getSpace(userId: DomainId, spaceId: DomainId): Space {
        val spaceEntity = spaces[spaceId.value] ?: throw IllegalArgumentException("Space not found")
        return Space(
            id = DomainId(spaceEntity.id),
            userId = DomainId(spaceEntity.userId),
            name = spaceEntity.name
        )
    }

    override fun update(space: Space): Space {
        val spaceEntity = spaces[space.id.value] ?: throw IllegalArgumentException("Space not found")
        val updatedSpaceEntity = spaceEntity.copy(
            name = space.name,
            updatedAt = LocalDateTime.now()
        )
        spaces[space.id.value] = updatedSpaceEntity
        return space
    }


    data class SpaceEntity(
        val id: String,
        val userId: String,
        val name: String,
        val templateType: Int,
        val createdAt: LocalDateTime = LocalDateTime.now(),
        val updatedAt: LocalDateTime = LocalDateTime.now()
    )

}