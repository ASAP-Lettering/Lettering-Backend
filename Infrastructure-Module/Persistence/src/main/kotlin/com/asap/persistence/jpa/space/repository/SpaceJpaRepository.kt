package com.asap.persistence.jpa.space.repository

import com.asap.persistence.jpa.common.EntityStatus
import com.asap.persistence.jpa.space.entity.SpaceEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface SpaceJpaRepository : JpaRepository<SpaceEntity, String> {
    @Query(
        """
        SELECT s
        FROM SpaceEntity s
        WHERE s.id = :id
        AND s.userId = :userId
        AND s.spaceStatus = :status
    """,
    )
    fun findByIdAndUserId(
        id: String,
        userId: String,
        status: EntityStatus,
    ): SpaceEntity?

    @Query(
        """
        SELECT COUNT(s)
        FROM SpaceEntity s
        WHERE s.userId = :userId
        AND s.spaceStatus = :entityStatus
    """,
    )
    fun countByUserId(
        userId: String,
        entityStatus: EntityStatus,
    ): Long

    @Query(
        """
        SELECT s
        FROM SpaceEntity s
        WHERE s.userId = :userId
        AND s.spaceStatus = :entityStatus
        """,
    )
    fun findAllByUserId(
        userId: String,
        entityStatus: EntityStatus,
    ): List<SpaceEntity>

    @Query(
        """
        SELECT s
        FROM SpaceEntity s
        WHERE s.userId = :userId
        AND s.id in :spaceIds
        AND s.spaceStatus = :entityStatus
    """,
    )
    fun findAllBy(
        userId: String,
        spaceIds: List<String>,
        entityStatus: EntityStatus,
    ): List<SpaceEntity>

    @Query("""
        SELECT s
        FROM SpaceEntity s
        WHERE s.isMain = true
        AND s.userId = :userId
        AND s.spaceStatus = :entityStatus
    """)
    fun findMainSpace(
        userId: String,
        entityStatus: EntityStatus
    ): SpaceEntity?

    @Modifying
    @Query(
        """
        UPDATE SpaceEntity s
        SET s.spaceStatus = :entityStatus
        WHERE s.id in :id
        AND s.userId = :userId
    """,
    )
    fun updateSpaceEntityStatusByIdAndUserId(
        id: List<String>,
        userId: String,
        entityStatus: EntityStatus,
    )
}

fun SpaceJpaRepository.findAllActiveSpaceByUserId(userId: String): List<SpaceEntity> = findAllByUserId(userId, EntityStatus.ACTIVE)

fun SpaceJpaRepository.findAllActiveSpaceByUserIdAndIds(
    userId: String,
    spaceIds: List<String>,
): List<SpaceEntity> = findAllBy(userId, spaceIds, EntityStatus.ACTIVE)

fun SpaceJpaRepository.deleteByUserIdAndId(
    userId: String,
    id: String,
) {
    updateSpaceEntityStatusByIdAndUserId(listOf(id), userId, EntityStatus.DELETED)
}

fun SpaceJpaRepository.deleteAllByUserIdAndIds(
    userId: String,
    ids: List<String>,
) {
    updateSpaceEntityStatusByIdAndUserId(ids, userId, EntityStatus.DELETED)
}

fun SpaceJpaRepository.findActiveSpaceByIdAndUserId(
    id: String,
    userId: String,
): SpaceEntity? = findByIdAndUserId(id, userId, EntityStatus.ACTIVE)

fun SpaceJpaRepository.findActiveMainSpace(
    userId: String,
): SpaceEntity? = findMainSpace(userId, EntityStatus.ACTIVE)

fun SpaceJpaRepository.countActiveSpaceByUserId(userId: String): Long = countByUserId(userId, EntityStatus.ACTIVE)
