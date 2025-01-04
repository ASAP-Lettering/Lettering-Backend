package com.asap.persistence.jpa.letter.adapter

import com.asap.application.letter.port.out.LetterLogManagementPort
import com.asap.domain.common.DomainId
import com.asap.domain.letter.entity.LetterLog
import com.asap.persistence.jpa.letter.LetterLogMapper
import com.asap.persistence.jpa.letter.repository.LetterLogJpaRepository
import org.springframework.stereotype.Repository

@Repository
class LetterLogManagementJpaAdapter(
    private val letterLogJpaRepository: LetterLogJpaRepository
) : LetterLogManagementPort {
    override fun save(log: LetterLog): LetterLog {
        return letterLogJpaRepository.save(LetterLogMapper.toEntity(log)).let { LetterLogMapper.toDomain(it) }
    }

    override fun findAll(): List<LetterLog> {
        return letterLogJpaRepository.findAll().map { LetterLogMapper.toDomain(it) }
    }

    override fun findLatestByLetterId(letterId: DomainId): LetterLog? {
        return letterLogJpaRepository.findLatestBy(letterId.value)?.let { LetterLogMapper.toDomain(it) }
    }
}