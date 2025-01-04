package com.asap.persistence.jpa.letter.repository

import com.asap.persistence.jpa.letter.entity.LetterLogEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface LetterLogJpaRepository: JpaRepository<LetterLogEntity, String> {
    @Query("""
        select l from LetterLogEntity l
        where l.targetLetterId = :letterId
        order by l.createdAt desc
        limit 1
    """)
    fun findLatestBy(letterId: String): LetterLogEntity?
}