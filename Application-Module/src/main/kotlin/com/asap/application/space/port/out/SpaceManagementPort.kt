package com.asap.application.space.port.out

import com.asap.domain.common.DomainId
import com.asap.domain.space.entity.IndexedSpace
import com.asap.domain.space.entity.MainSpace
import com.asap.domain.space.entity.Space

interface SpaceManagementPort {
    fun getMainSpace(userId: DomainId): MainSpace

    fun getSpaceNotNull(
        userId: DomainId,
        spaceId: DomainId,
    ): Space

    fun getAllIndexedSpace(userId: DomainId): List<IndexedSpace>

    fun save(space: Space): Space

    fun update(space: Space): Space

    fun updateIndexes(
        userId: DomainId,
        orders: List<IndexedSpace>,
    )

    fun deleteById(
        userId: DomainId,
        spaceId: DomainId,
    )

    fun deleteAllBySpaceIds(
        userId: DomainId,
        spaceIds: List<DomainId>,
    )
}
