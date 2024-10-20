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

    fun getIndexedSpaceNotNull(
        userId: DomainId,
        spaceId: DomainId,
    ): IndexedSpace

    fun getAllIndexedSpace(userId: DomainId): List<IndexedSpace>

    fun getAllSpaceBy(
        userId: DomainId,
        spaceIds: List<DomainId>,
    ): List<Space>

    fun getAllSpaceBy(userId: DomainId): List<Space>

    fun save(space: Space): Space

    fun update(space: Space): Space

    fun update(indexedSpace: IndexedSpace): IndexedSpace

    fun updateIndexes(
        userId: DomainId,
        orders: List<IndexedSpace>,
    )

    fun deleteBy(space: Space)

    fun countByUserId(userId: DomainId): Long
}
