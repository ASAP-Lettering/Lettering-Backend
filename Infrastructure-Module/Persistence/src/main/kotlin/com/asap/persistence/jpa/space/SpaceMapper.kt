package com.asap.persistence.jpa.space

import com.asap.domain.common.DomainId
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
            index = spaceEntity.index,
            isMain = spaceEntity.isMain,
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
    )
}
