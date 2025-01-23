package com.asap.persistence.jpa.letter.repository

import com.asap.persistence.jpa.letter.entity.ReceiveDraftLetterEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ReceiveDraftLetterJpaRepository : JpaRepository<ReceiveDraftLetterEntity, String>{
}