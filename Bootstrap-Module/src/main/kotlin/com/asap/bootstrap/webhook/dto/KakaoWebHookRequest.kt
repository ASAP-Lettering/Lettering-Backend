package com.asap.bootstrap.webhook.dto

import com.asap.bootstrap.webhook.dto.KakaoChatType.entries
import com.asap.domain.letter.entity.LetterLogType
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

data class KakaoWebHookRequest(
    @field:JsonProperty("CHAT_TYPE")
    val chatType: KakaoChatType,
    @field:JsonProperty("HASH_CHAT_ID")
    val hashChatId: String,
    @field:JsonProperty("TEMPLATE_ID")
    val templateId: String,
    val requestType: LetterLogType,
    val requestId: String
)

@JsonDeserialize(using = KakaoChatType.KakaoChatTypeDeserializer::class)
enum class KakaoChatType(
    val value: String
) {
    MEMO_CHAT("MemoChat"),
    DIRECT_CHAT("DirectChat"),
    MULTI_CHAT("MultiChat"),
    OPEN_DIRECT_CHAT("OpenDirectChat"),
    OPEN_MULTI_CHAT("OpenMultiChat"),
    ;

    companion object {
        fun parse(value: String): KakaoChatType {
            return entries.firstOrNull { it.value == value || it.name == value }
                ?: throw IllegalArgumentException("Unknown KakaoChatType: $value")
        }
    }

    internal class KakaoChatTypeDeserializer : JsonDeserializer<KakaoChatType>() {
        override fun deserialize(p: JsonParser, ctxt: DeserializationContext): KakaoChatType {
            return parse(p.text)
        }
    }

}