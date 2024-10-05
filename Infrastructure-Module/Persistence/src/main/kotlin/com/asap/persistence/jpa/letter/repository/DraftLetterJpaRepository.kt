package com.asap.persistence.jpa.letter.repository

import com.asap.persistence.jpa.letter.entity.DraftLetterEntity
import org.springframework.data.jpa.repository.JpaRepository

interface DraftLetterJpaRepository : JpaRepository<DraftLetterEntity, String> {
    fun findByIdAndOwnerId(
        draftId: String,
        ownerId: String,
    ): DraftLetterEntity?
}
