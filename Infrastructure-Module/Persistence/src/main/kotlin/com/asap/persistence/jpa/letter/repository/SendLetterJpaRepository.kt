package com.asap.persistence.jpa.letter.repository

import com.asap.domain.letter.enums.LetterStatus
import com.asap.persistence.jpa.common.EntityStatus
import com.asap.persistence.jpa.letter.entity.SendLetterEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
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

    @Query(
        """
        SELECT s
        FROM SendLetterEntity s
        WHERE s.senderId = :senderId
        AND s.entityStatus = :entityStatus
    """,
    )
    fun findAllBy(
        senderId: String,
        entityStatus: EntityStatus,
    ): List<SendLetterEntity>

    @Query(
        """
        SELECT s
        FROM SendLetterEntity s
        WHERE s.senderId = :senderId
        AND s.id IN :letterIds
        AND s.entityStatus = :entityStatus
    """,
    )
    fun findAllBy(
        senderId: String,
        letterIds: List<String>,
        entityStatus: EntityStatus,
    ): List<SendLetterEntity>

    @Query(
        """
        SELECT s
        FROM SendLetterEntity s
        WHERE s.id = :letterId
        AND s.senderId = :senderId
        AND s.entityStatus = :entityStatus
    """,
    )
    fun findBy(
        senderId: String,
        letterId: String,
        entityStatus: EntityStatus,
    ): SendLetterEntity?

    fun existsByLetterCodeAndReceiverId(
        letterCode: String,
        receiverId: String,
    ): Boolean

    @Modifying
    @Query(
        """
        UPDATE SendLetterEntity s
        SET s.entityStatus = :entityStatus
        WHERE s.id = :id
    """,
    )
    fun updateEntityStatus(
        id: String,
        entityStatus: EntityStatus,
    )
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

fun SendLetterJpaRepository.findActiveSendLetterByIdAndSenderId(
    letterId: String,
    senderId: String,
): SendLetterEntity? = findBy(senderId, letterId, EntityStatus.ACTIVE)

fun SendLetterJpaRepository.findAllActiveSendLetterBySenderId(senderId: String): List<SendLetterEntity> =
    findAllBy(senderId, EntityStatus.ACTIVE)

fun SendLetterJpaRepository.findAllActiveSendLetterBySenderIdAndLetterIds(
    senderId: String,
    letterIds: List<String>,
): List<SendLetterEntity> = findAllBy(senderId, letterIds, EntityStatus.ACTIVE)

fun SendLetterJpaRepository.deleteBy(sendLetterEntity: SendLetterEntity) {
    updateEntityStatus(sendLetterEntity.id, EntityStatus.DELETED)
}
