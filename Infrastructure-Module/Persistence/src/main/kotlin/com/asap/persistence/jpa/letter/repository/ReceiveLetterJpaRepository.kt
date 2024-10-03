package com.asap.persistence.jpa.letter.repository

import com.asap.persistence.jpa.common.EntityStatus
import com.asap.persistence.jpa.letter.entity.ReceiveLetterEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface ReceiveLetterJpaRepository : JpaRepository<ReceiveLetterEntity, String> {
    @Query(
        """
        SELECT r
        FROM ReceiveLetterEntity r
        WHERE r.receiverId = :receiverId
        AND ((r.spaceId is not null and :isSpaceLetter = true) or (r.spaceId is null and :isSpaceLetter = false)) 
        AND r.entityStatus = :entityStatus
        """,
    )
    fun findAllByReceiverId(
        receiverId: String,
        isSpaceLetter: Boolean,
        entityStatus: EntityStatus,
    ): List<ReceiveLetterEntity>

    @Query(
        """
        SELECT r
        FROM ReceiveLetterEntity r
        WHERE r.spaceId = :spaceId
        AND r.receiverId = :receiverId
        AND r.entityStatus = :entityStatus
    """,
    )
    fun findAllBySpaceIdAndReceiverId(
        spaceId: String,
        receiverId: String,
        entityStatus: EntityStatus,
    ): List<ReceiveLetterEntity>

    @Query(
        """
        SELECT r
        FROM ReceiveLetterEntity r
        WHERE r.id = :letterId
        AND ((r.spaceId is not null and :isSpaceLetter = true) or (r.spaceId is null and :isSpaceLetter = false))
        AND r.entityStatus = :entityStatus
    """,
    )
    fun findBy(
        letterId: String,
        isSpaceLetter: Boolean,
        entityStatus: EntityStatus,
    ): ReceiveLetterEntity?

    @Query(
        """
        SELECT r
        FROM ReceiveLetterEntity r
        WHERE r.id = :letterId
        AND r.receiverId = :receiverId
        AND ((r.spaceId is not null and :isSpaceLetter = true) or (r.spaceId is null and :isSpaceLetter = false))
        AND r.entityStatus = :entityStatus
    """,
    )
    fun findBy(
        letterId: String,
        receiverId: String,
        isSpaceLetter: Boolean,
        entityStatus: EntityStatus,
    ): ReceiveLetterEntity?

    fun countBySpaceId(spaceId: String): Int

    fun findAllBySpaceIdAndReceiverId(
        spaceId: String,
        receiverId: String,
        pageable: Pageable,
    ): Page<ReceiveLetterEntity>

    @Modifying
    @Query(
        """
        UPDATE ReceiveLetterEntity r
        SET r.entityStatus = :entityStatus
        WHERE r.id = :id
    """,
    )
    fun updateEntityStatusById(
        id: String,
        entityStatus: EntityStatus,
    )
}

fun ReceiveLetterJpaRepository.findAllIndependentByReceiverId(receiverId: String): List<ReceiveLetterEntity> =
    findAllByReceiverId(receiverId, false, EntityStatus.ACTIVE)

fun ReceiveLetterJpaRepository.findAllSpaceBySpaceIdAndReceiverId(
    spaceId: String,
    receiverId: String,
): List<ReceiveLetterEntity> = findAllBySpaceIdAndReceiverId(spaceId, receiverId, EntityStatus.ACTIVE)

fun ReceiveLetterJpaRepository.findIndependentById(letterId: String): ReceiveLetterEntity? = findBy(letterId, false, EntityStatus.ACTIVE)

fun ReceiveLetterJpaRepository.findSpaceByIdAndReceiverId(
    letterId: String,
    receiverId: String,
): ReceiveLetterEntity? = findBy(letterId, receiverId, true, EntityStatus.ACTIVE)

fun ReceiveLetterJpaRepository.findIndependentByIdAndReceiverId(
    letterId: String,
    receiverId: String,
): ReceiveLetterEntity? = findBy(letterId, receiverId, false, EntityStatus.ACTIVE)

fun ReceiveLetterJpaRepository.deleteByLetterId(id: String) {
    updateEntityStatusById(id, EntityStatus.DELETED)
}
