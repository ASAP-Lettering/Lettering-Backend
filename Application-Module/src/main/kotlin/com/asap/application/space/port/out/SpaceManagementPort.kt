package com.asap.application.space.port.out

import com.asap.domain.common.DomainId
import com.asap.domain.space.entity.Space

interface SpaceManagementPort {
    fun getMainSpace(userId: DomainId): Space

    fun getSpaceNotNull(
        userId: DomainId,
        spaceId: DomainId,
    ): Space

    fun getAllSpaceBy(
        userId: DomainId,
        spaceIds: List<DomainId>,
    ): List<Space>

    fun getAllSpaceBy(userId: DomainId): List<Space>

    fun save(space: Space): Space

    fun saveAll(spaces: List<Space>): List<Space>

    fun update(space: Space): Space

    fun deleteBy(space: Space)

    fun countByUserId(userId: DomainId): Long
}
