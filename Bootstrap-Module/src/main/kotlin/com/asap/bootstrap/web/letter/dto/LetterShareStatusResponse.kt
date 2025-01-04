package com.asap.bootstrap.web.letter.dto

import com.asap.bootstrap.webhook.dto.KakaoChatType

data class LetterShareStatusResponse(
    val isShared: Boolean,
    val letterId: String?,
    val shareTarget: KakaoChatType?,
) {
    companion object{
        fun success(
            letterId: String,
            shareTarget: KakaoChatType
        ): LetterShareStatusResponse {
            return LetterShareStatusResponse(
                isShared = true,
                letterId = letterId,
                shareTarget = shareTarget
            )
        }

        fun fail(): LetterShareStatusResponse {
            return LetterShareStatusResponse(
                isShared = false,
                letterId = null,
                shareTarget = null
            )
        }
    }
}