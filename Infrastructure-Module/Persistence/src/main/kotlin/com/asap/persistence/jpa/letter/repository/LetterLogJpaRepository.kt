package com.asap.persistence.jpa.letter.repository

import com.asap.persistence.jpa.letter.entity.LetterLogEntity
import org.springframework.data.jpa.repository.JpaRepository

interface LetterLogJpaRepository: JpaRepository<LetterLogEntity, String> {
}