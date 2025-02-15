package com.asap.persistence.jpa.space.adapter

import com.asap.application.space.exception.SpaceException
import com.asap.application.space.port.out.SpaceManagementPort
import com.asap.common.event.EventPublisher
import com.asap.domain.common.DomainId
import com.asap.domain.space.entity.Space
import com.asap.persistence.jpa.space.SpaceMapper
import com.asap.persistence.jpa.space.repository.*
import org.springframework.stereotype.Repository

@Repository
class SpaceManagementJpaAdapter(
    private val spaceJpaRepository: SpaceJpaRepository,
    private val eventPublisher: EventPublisher,
) : SpaceManagementPort {
    override fun getMainSpace(userId: DomainId): Space =
        spaceJpaRepository
            .findActiveMainSpace(userId.value)
            ?.let {
                SpaceMapper.toSpace(it)
            }
            ?: throw SpaceException.MainSpaceNotFoundException()


    override fun getSpaceNotNull(
        userId: DomainId,
        spaceId: DomainId,
    ): Space =
        spaceJpaRepository.findActiveSpaceByIdAndUserId(spaceId.value, userId.value)?.let {
            SpaceMapper.toSpace(it)
        } ?: throw SpaceException.SpaceNotFoundException()

    override fun getAllSpaceBy(
        userId: DomainId,
        spaceIds: List<DomainId>,
    ): List<Space> {
        val spaces =
            spaceJpaRepository.findAllActiveSpaceByUserIdAndIds(
                userId = userId.value,
                spaceIds = spaceIds.map { it.value },
            )
        return spaces.map {
            SpaceMapper.toSpace(it)
        }
    }

    override fun getAllSpaceBy(userId: DomainId): List<Space> {
        val spaces = spaceJpaRepository.findAllActiveSpaceByUserId(userId.value)
        return spaces.map {
            SpaceMapper.toSpace(it)
        }
    }

    override fun save(space: Space): Space {
        val entity =
            SpaceMapper.toSpaceEntity(
                space = space,
            )
        return spaceJpaRepository
            .save(entity)
            .let {
                SpaceMapper.toSpace(it)
            }.also {
                eventPublisher.publishAll(space.pullEvents())
            }
    }

    override fun saveAll(spaces: List<Space>): List<Space> {
        val entities = spaces.map {
            SpaceMapper.toSpaceEntity(
                space = it,
            )
        }
        return spaceJpaRepository
            .saveAll(entities)
            .map {
                SpaceMapper.toSpace(it)
            }.also {
                eventPublisher.publishAll(spaces.flatMap { it.pullEvents() })
            }
    }

    override fun update(space: Space): Space =
        spaceJpaRepository.findActiveSpaceByIdAndUserId(space.id.value, space.userId.value)?.let {
            it.update(space)
            spaceJpaRepository.save(it)

            eventPublisher.publishAll(space.pullEvents())

            SpaceMapper.toSpace(it)
        } ?: throw SpaceException.SpaceNotFoundException()

    override fun deleteBy(space: Space) {
        spaceJpaRepository.deleteByUserIdAndId(
            userId = space.userId.value,
            id = space.id.value,
        )
        eventPublisher.publishAll(space.pullEvents())
    }

    override fun countByUserId(userId: DomainId): Long = spaceJpaRepository.countActiveSpaceByUserId(userId.value)
}
