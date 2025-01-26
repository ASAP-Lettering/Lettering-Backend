package com.asap.persistence.jpa.letter.adapter

import com.asap.application.letter.exception.LetterException
import com.asap.application.letter.port.out.ReceiveDraftLetterManagementPort
import com.asap.domain.common.DomainId
import com.asap.domain.letter.entity.ReceiveDraftLetter
import com.asap.persistence.jpa.letter.ReceiveDraftLetterMapper
import com.asap.persistence.jpa.letter.repository.ReceiveDraftLetterJpaRepository
import org.springframework.stereotype.Repository

@Repository
class ReceiveDraftLetterManagementJpaAdapter(
    private val receiveDraftLetterJpaRepository: ReceiveDraftLetterJpaRepository,
) : ReceiveDraftLetterManagementPort {
    override fun save(receiveDraftLetter: ReceiveDraftLetter): ReceiveDraftLetter {
        val receiveDraftLetterEntity = ReceiveDraftLetterMapper.toEntity(receiveDraftLetter)
        return receiveDraftLetterJpaRepository.save(receiveDraftLetterEntity)
            .let { ReceiveDraftLetterMapper.toDomain(it) }
    }

    override fun getDraftLetterNotNull(
        draftId: DomainId,
        ownerId: DomainId
    ): ReceiveDraftLetter {
        return receiveDraftLetterJpaRepository
            .findBy(draftId.value, ownerId.value)
            ?.let { ReceiveDraftLetterMapper.toDomain(it) }
            ?: throw LetterException.DraftLetterNotFoundException()
    }

    override fun getAllDrafts(ownerId: DomainId): List<ReceiveDraftLetter> {
        return receiveDraftLetterJpaRepository
            .findAllBy(ownerId.value)
            .map { ReceiveDraftLetterMapper.toDomain(it) }
    }
}