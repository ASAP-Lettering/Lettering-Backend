package com.asap.persistence.jpa.space

import com.asap.domain.common.DomainId
import com.asap.domain.space.entity.Space
import com.asap.persistence.jpa.space.entity.SpaceEntity

object SpaceMapper {
    fun toSpace(spaceEntity: SpaceEntity) =
        Space(
            id = DomainId(spaceEntity.id),
            userId = DomainId(spaceEntity.userId),
            name = spaceEntity.name,
            templateType = spaceEntity.templateType,
            index = spaceEntity.index,
            isMain = spaceEntity.isMain,
            createdAt = spaceEntity.createdAt,
            updatedAt = spaceEntity.updatedAt,
        )

    fun toSpaceEntity(
        space: Space,
    ) = SpaceEntity(
        id = space.id.value,
        userId = space.userId.value,
        name = space.name,
        templateType = space.templateType,
        index = space.index,
        isMain = space.isMain,
        createdAt = space.createdAt,
        updatedAt = space.updatedAt,
    )
}
