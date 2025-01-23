package com.asap.persistence.jpa.letter.adapter

import com.asap.application.letter.port.out.ReceiveDraftLetterManagementPort
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
}