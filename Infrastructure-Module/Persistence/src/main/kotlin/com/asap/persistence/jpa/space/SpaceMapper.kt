package com.asap.persistence.jpa.space

import com.asap.domain.common.DomainId
import com.asap.domain.space.entity.IndexedSpace
import com.asap.domain.space.entity.MainSpace
import com.asap.domain.space.entity.Space
import com.asap.persistence.jpa.space.entity.SpaceEntity

object SpaceMapper {
    fun toMainSpace(spaceEntity: SpaceEntity): MainSpace =
        MainSpace(
            id = DomainId(spaceEntity.id),
        )

    fun toSpace(spaceEntity: SpaceEntity) =
        Space(
            id = DomainId(spaceEntity.id),
            userId = DomainId(spaceEntity.userId),
            name = spaceEntity.name,
            templateType = spaceEntity.templateType,
        )

    fun toIndexedSpace(spaceEntity: SpaceEntity) =
        IndexedSpace(
            id = DomainId(spaceEntity.id),
            userId = DomainId(spaceEntity.userId),
            name = spaceEntity.name,
            templateType = spaceEntity.templateType,
            index = spaceEntity.index,
        )

    fun toSpaceEntity(
        space: Space,
        index: Int,
    ) = SpaceEntity(
        id = space.id.value,
        userId = space.userId.value,
        name = space.name,
        templateType = space.templateType,
        index = index,
    )
}
