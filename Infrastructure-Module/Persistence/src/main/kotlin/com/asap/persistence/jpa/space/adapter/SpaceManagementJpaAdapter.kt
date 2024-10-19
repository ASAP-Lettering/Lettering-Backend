package com.asap.persistence.jpa.space.adapter

import com.asap.application.space.exception.SpaceException
import com.asap.application.space.port.out.SpaceManagementPort
import com.asap.domain.common.DomainId
import com.asap.domain.space.entity.IndexedSpace
import com.asap.domain.space.entity.MainSpace
import com.asap.domain.space.entity.Space
import com.asap.persistence.jpa.space.SpaceMapper
import com.asap.persistence.jpa.space.repository.*
import org.springframework.stereotype.Repository

@Repository
class SpaceManagementJpaAdapter(
    private val spaceJpaRepository: SpaceJpaRepository,
) : SpaceManagementPort {
    override fun getMainSpace(userId: DomainId): MainSpace =
        spaceJpaRepository
            .findAllActiveSpaceByUserId(userId.value)
            .first {
                it.index == 0
            }.let {
                SpaceMapper.toMainSpace(it)
            }

    override fun getSpaceNotNull(
        userId: DomainId,
        spaceId: DomainId,
    ): Space =
        spaceJpaRepository.findActiveSpaceByIdAndUserId(spaceId.value, userId.value)?.let {
            SpaceMapper.toSpace(it)
        } ?: throw SpaceException.SpaceNotFoundException()

    override fun getAllIndexedSpace(userId: DomainId): List<IndexedSpace> =
        spaceJpaRepository
            .findAllActiveSpaceByUserId(userId.value)
            .map {
                SpaceMapper.toIndexedSpace(it)
            }.sortedBy { it.index }

    override fun save(space: Space): Space {
        val entity =
            SpaceMapper.toSpaceEntity(
                space = space,
                index = spaceJpaRepository.countActiveSpaceByUserId(space.userId.value).toInt(),
            )
        return spaceJpaRepository.save(entity).let {
            SpaceMapper.toSpace(it)
        }
    }

    override fun update(space: Space): Space =
        spaceJpaRepository.findActiveSpaceByIdAndUserId(space.id.value, space.userId.value)?.let {
            it.update(space)
            spaceJpaRepository.save(it)
            SpaceMapper.toSpace(it)
        } ?: throw SpaceException.SpaceNotFoundException()

    override fun update(indexedSpace: IndexedSpace): IndexedSpace {
        spaceJpaRepository.findActiveSpaceByIdAndUserId(indexedSpace.id.value, indexedSpace.userId.value)?.let {
            it.index = indexedSpace.index
            spaceJpaRepository.save(it)
        } ?: throw SpaceException.SpaceNotFoundException()
        return indexedSpace
    }

    override fun updateIndexes(
        userId: DomainId,
        orders: List<IndexedSpace>,
    ) {
        val spaces = spaceJpaRepository.findAllActiveSpaceByUserId(userId.value)
        orders.forEach { order ->
            spaces.find { it.id == order.id.value }?.let {
                it.index = order.index
                spaceJpaRepository.save(it)
            }
        }
    }

    override fun deleteById(
        userId: DomainId,
        spaceId: DomainId,
    ) {
        spaceJpaRepository.deleteByUserIdAndId(
            userId = userId.value,
            id = spaceId.value,
        )
    }

    override fun deleteAllBySpaceIds(
        userId: DomainId,
        spaceIds: List<DomainId>,
    ) {
        spaceJpaRepository.deleteAllByUserIdAndIds(
            userId = userId.value,
            ids = spaceIds.map { it.value },
        )
    }

    override fun deleteAllByUserId(userId: DomainId) {
        spaceJpaRepository.deleteAllByUserIdAndIds(
            userId = userId.value,
            ids = spaceJpaRepository.findAllActiveSpaceByUserId(userId.value).map { it.id },
        )
    }

    override fun countByUserId(userId: DomainId): Long = spaceJpaRepository.countActiveSpaceByUserId(userId.value)
}
