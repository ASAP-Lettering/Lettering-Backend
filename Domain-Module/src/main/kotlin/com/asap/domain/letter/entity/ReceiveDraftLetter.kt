package com.asap.domain.letter.entity

import com.asap.domain.common.BaseEntity
import com.asap.domain.common.DomainId
import java.time.LocalDateTime

class ReceiveDraftLetter(
    id: DomainId,
    var content: String,
    var senderName: String,
    val ownerId: DomainId,
    var images: List<String>,
    var lastUpdated: LocalDateTime,
    val type: ReceiveDraftLetterType, // TODO: 상속 구조를 통한 타입 구분 생각해보기
    createdAt: LocalDateTime,
) : BaseEntity(id, createdAt, lastUpdated) {
    companion object {
        fun default(ownerId: DomainId) =
            ReceiveDraftLetter(
                id = DomainId.generate(),
                ownerId = ownerId,
                content = "",
                senderName = "",
                images = emptyList(),
                type = ReceiveDraftLetterType.PHYSICAL,
                lastUpdated = LocalDateTime.now(),
                createdAt = LocalDateTime.now(),
            )
    }

    fun update(
        content: String,
        senderName: String,
        images: List<String>,
    ) {
        this.content = content
        this.senderName = senderName
        this.images = images
        this.lastUpdated = LocalDateTime.now()
        updateTime()
    }
}

enum class ReceiveDraftLetterType{
    PHYSICAL,
}