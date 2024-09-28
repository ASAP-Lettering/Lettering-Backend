package com.asap.application.letter.port.out.memory

import com.asap.application.letter.exception.LetterException
import com.asap.application.letter.port.out.SendLetterManagementPort
import com.asap.domain.common.DomainId
import com.asap.domain.letter.entity.SendLetter
import com.asap.domain.letter.vo.LetterContent
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
@Primary
class MemorySendLetterManagementAdapter : SendLetterManagementPort {
    private val sendLetters = mutableListOf<SendLetterEntity>()

    override fun save(sendLetter: SendLetter) {
        sendLetters.add(SendLetterEntity.fromSendLetter(sendLetter))
    }

    override fun getLetterNotNull(letterId: DomainId): SendLetter =
        matchingNotExpired { this.id == letterId.value }?.toSendLetter()
            ?: throw LetterException.SendLetterNotFoundException()

    override fun getLetterByCodeNotNull(letterCode: String): SendLetter =
        matchingNotExpired { this.letterCode == letterCode }?.toSendLetter()
            ?: throw LetterException.SendLetterNotFoundException()

    override fun getReadLetterNotNull(
        receiverId: DomainId,
        letterCode: String,
    ): SendLetter {
        sendLetters.find { it.letterCode == letterCode && it.receiverId == receiverId.value }?.let {
            return it.toSendLetter()
        } ?: throw LetterException.SendLetterNotFoundException()
    }

    override fun getReadLetterNotNull(
        receiverId: DomainId,
        letterId: DomainId,
    ): SendLetter {
        sendLetters.find { it.id == letterId.value && it.receiverId == receiverId.value }?.let {
            return it.toSendLetter()
        } ?: throw LetterException.SendLetterNotFoundException()
    }

    override fun verifiedLetter(
        receiverId: DomainId,
        letterCode: String,
    ): Boolean = matching { this.letterCode == letterCode }?.receiverId.isNullOrBlank().not()

    private fun matchingNotExpired(query: SendLetterEntity.() -> Boolean): SendLetterEntity? = matching { this.isExpired.not() and query() }

    private fun matchingExpired(query: SendLetterEntity.() -> Boolean): SendLetterEntity? = matching { this.isExpired and query() }

    private fun matching(query: SendLetterEntity.() -> Boolean): SendLetterEntity? = sendLetters.find { query(it) }

    data class SendLetterEntity(
        val id: String,
        val receiverName: String,
        val content: String,
        val images: List<String>,
        val templateType: Int,
        val senderId: String,
        val letterCode: String,
        var isExpired: Boolean,
        var receiverId: String?,
        val createdAt: LocalDateTime,
    ) {
        fun toSendLetter(): SendLetter =
            SendLetter(
                id = DomainId(id),
                receiverName = receiverName,
                content =
                    LetterContent(
                        content = content,
                        images = images,
                        templateType = templateType,
                    ),
                senderId = DomainId(senderId),
                letterCode = letterCode,
                createdAt = createdAt,
            )

        companion object {
            fun fromSendLetter(sendLetter: SendLetter): SendLetterEntity =
                SendLetterEntity(
                    id = sendLetter.id.value,
                    receiverName = sendLetter.receiverName,
                    content = sendLetter.content.content,
                    images = sendLetter.content.images,
                    templateType = sendLetter.content.templateType,
                    senderId = sendLetter.senderId.value,
                    letterCode = sendLetter.letterCode,
                    isExpired = false,
                    receiverId = sendLetter.receiverId?.value,
                    createdAt = sendLetter.createdAt,
                )
        }
    }
}
