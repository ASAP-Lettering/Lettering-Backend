package com.asap.domain.letter.vo

import com.asap.domain.common.DomainId

data class SenderInfo(
    var senderId: DomainId? = null,
    var senderName: String,
) {
    fun update(senderName: String) {
        this.senderName = senderName
    }

    fun delete()  {
        this.senderName = ""
        this.senderId = null
    }
}
