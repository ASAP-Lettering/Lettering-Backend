package com.asap.persistence.jpa.letter.repository

import com.asap.persistence.jpa.letter.entity.ReceiveDraftLetterEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ReceiveDraftLetterJpaRepository : JpaRepository<ReceiveDraftLetterEntity, String>{
    @Query("""
        SELECT rdl
        FROM ReceiveDraftLetterEntity rdl
        WHERE rdl.id = :id
        AND rdl.ownerId = :ownerId
    """)
    fun findBy(id: String, ownerId: String): ReceiveDraftLetterEntity?

    @Query("""
        SELECT rdl
        FROM ReceiveDraftLetterEntity rdl
        WHERE rdl.ownerId = :ownerId
    """)
    fun findAllBy(ownerId: String): List<ReceiveDraftLetterEntity>

    @Query("""
        SELECT COUNT(rdl.id)
        FROM ReceiveDraftLetterEntity rdl
        WHERE rdl.ownerId = :ownerId
    """)
    fun countBy(ownerId: String): Int
}