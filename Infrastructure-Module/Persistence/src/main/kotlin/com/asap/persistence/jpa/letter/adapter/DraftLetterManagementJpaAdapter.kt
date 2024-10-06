package com.asap.persistence.jpa.letter.adapter

import com.asap.application.letter.exception.LetterException
import com.asap.application.letter.port.out.DraftLetterManagementPort
import com.asap.domain.common.DomainId
import com.asap.domain.letter.entity.DraftLetter
import com.asap.persistence.jpa.letter.DraftLetterMapper
import com.asap.persistence.jpa.letter.repository.DraftLetterJpaRepository
import org.springframework.stereotype.Repository

@Repository
class DraftLetterManagementJpaAdapter(
    private val draftLetterJpaRepository: DraftLetterJpaRepository,
) : DraftLetterManagementPort {
    override fun save(draftLetter: DraftLetter): DraftLetter =
        draftLetterJpaRepository
            .save(DraftLetterMapper.toEntity(draftLetter))
            .let { DraftLetterMapper.toDomain(it) }

    override fun getDraftLetterNotNull(
        draftId: DomainId,
        ownerId: DomainId,
    ): DraftLetter =
        draftLetterJpaRepository
            .findByIdAndOwnerId(draftId.value, ownerId.value)
            ?.let { DraftLetterMapper.toDomain(it) }
            ?: throw LetterException.DraftLetterNotFoundException()

    override fun getAllDrafts(ownerId: DomainId): List<DraftLetter> =
        draftLetterJpaRepository
            .findByOwnerId(ownerId.value)
            .map { DraftLetterMapper.toDomain(it) }

    override fun update(draftLetter: DraftLetter): DraftLetter =
        DraftLetterMapper
            .toEntity(draftLetter)
            .apply {
                draftLetterJpaRepository.save(this)
            }.let { DraftLetterMapper.toDomain(it) }

    override fun countDrafts(ownerId: DomainId): Int = draftLetterJpaRepository.countByOwnerId(ownerId.value)
}
