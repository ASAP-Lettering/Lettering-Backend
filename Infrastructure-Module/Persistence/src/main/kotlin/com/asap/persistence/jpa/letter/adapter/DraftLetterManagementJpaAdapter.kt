package com.asap.persistence.jpa.letter.adapter

import com.asap.application.letter.port.out.DraftLetterManagementPort
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
}
