package com.asap.domain.letter.vo

import com.asap.domain.common.DomainId

data class SenderInfo(
    val senderId: DomainId? = null,
    val senderName: String
) {
}