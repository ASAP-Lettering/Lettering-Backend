package com.asap.persistence.jpa.letter.repository

import com.asap.domain.letter.enums.LetterStatus
import com.asap.persistence.jpa.common.EntityStatus
import com.asap.persistence.jpa.letter.entity.SendLetterEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface SendLetterJpaRepository : JpaRepository<SendLetterEntity, String> {
    @Query(
        """
        SELECT s
        FROM SendLetterEntity s
        WHERE s.id = :id
        AND s.entityStatus = :entityStatus
    """,
    )
    fun findBy(
        id: String,
        entityStatus: EntityStatus,
    ): SendLetterEntity?

    @Query(
        """
        SELECT s
        FROM SendLetterEntity s
        WHERE s.letterCode = :letterCode
        AND s.entityStatus = :entityStatus
    """,
    )
    fun findByLetterCode(
        letterCode: String,
        entityStatus: EntityStatus,
    ): SendLetterEntity?

    @Query(
        """
        SELECT s
        FROM SendLetterEntity s
        WHERE s.id = :id
        AND s.receiverId = :receiverId
        AND s.letterStatus = :letterStatus
        AND s.entityStatus = :entityStatus
    """,
    )
    fun findByIdAndReceiverIdAndLetterStatus(
        id: String,
        receiverId: String,
        letterStatus: LetterStatus,
        entityStatus: EntityStatus,
    ): SendLetterEntity?

    @Query(
        """
        SELECT s
        FROM SendLetterEntity s
        WHERE s.letterCode = :letterCode
        AND s.receiverId = :receiverId
        AND s.letterStatus = :letterStatus
        AND s.entityStatus = :entityStatus
    """,
    )
    fun findByCodeAndReceiverIdAndLetterStatus(
        letterCode: String,
        receiverId: String,
        letterStatus: LetterStatus,
        entityStatus: EntityStatus,
    ): SendLetterEntity?

    fun existsByLetterCodeAndReceiverId(
        letterCode: String,
        receiverId: String,
    ): Boolean
}

fun SendLetterJpaRepository.findActiveSendLetterById(id: String): SendLetterEntity? = findBy(id, EntityStatus.ACTIVE)

fun SendLetterJpaRepository.findActiveSendLetterByCode(letterCode: String): SendLetterEntity? =
    findByLetterCode(letterCode, EntityStatus.ACTIVE)

fun SendLetterJpaRepository.findActiveSendLetterByCodeAndReceiverIdAndLetterStatus(
    code: String,
    receiverId: String,
    letterStatus: LetterStatus,
): SendLetterEntity? = findByCodeAndReceiverIdAndLetterStatus(code, receiverId, letterStatus, EntityStatus.ACTIVE)

fun SendLetterJpaRepository.findActiveSendLetterByIdAndReceiverIdAndLetterStatus(
    id: String,
    receiverId: String,
    letterStatus: LetterStatus,
): SendLetterEntity? = findByIdAndReceiverIdAndLetterStatus(id, receiverId, letterStatus, EntityStatus.ACTIVE)
