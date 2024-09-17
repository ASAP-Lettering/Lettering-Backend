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
class MemorySendLetterManagementAdapter(

) : SendLetterManagementPort {
    private val sendLetters = mutableListOf<SendLetterEntity>()


    override fun save(sendLetter: SendLetter) {
        sendLetters.add(SendLetterEntity.fromSendLetter(sendLetter))
    }

    override fun getLetterNotNull(letterId: DomainId): SendLetter {
        return matchingNotExpired { this.id == letterId.value }?.toSendLetter()
            ?: throw LetterException.SendLetterNotFoundException()
    }

    override fun getLetterByCodeNotNull(letterCode: String): SendLetter {
        return matchingNotExpired { this.letterCode == letterCode }?.toSendLetter()
            ?: throw LetterException.SendLetterNotFoundException()
    }

    override fun getExpiredLetterNotNull(receiverId: DomainId, letterCode: String): SendLetter {
        return matchingExpired { (this.letterCode == letterCode) and (this.receiverId == receiverId.value) }?.toSendLetter()
            ?: throw LetterException.SendLetterNotFoundException()
    }

    override fun getExpiredLetterNotNull(receiverId: DomainId, letterId: DomainId): SendLetter {
        return matchingExpired { (this.id == letterId.value) and (this.receiverId == receiverId.value) }?.toSendLetter()
            ?: throw LetterException.SendLetterNotFoundException()
    }

    override fun expireLetter(receiverId: DomainId, letterId: DomainId) {
        val sendLetter = matchingNotExpired { this.id == letterId.value }
            ?: throw LetterException.SendLetterNotFoundException()
        sendLetter.expire(receiverId.value)
    }

    override fun verifiedLetter(receiverId: DomainId, letterCode: String): Boolean {
        return matching { (this.letterCode == letterCode) and (this.receiverId == receiverId.value) }?.isExpired
            ?: false
    }

    override fun remove(letterId: DomainId) {
        sendLetters.removeIf { it.id == letterId.value }
    }

    private fun matchingNotExpired(query: SendLetterEntity.() -> Boolean): SendLetterEntity? {
        return matching { this.isExpired.not() and query() }
    }

    private fun matchingExpired(query: SendLetterEntity.() -> Boolean): SendLetterEntity? {
        return matching { this.isExpired and query() }
    }

    private fun matching(query: SendLetterEntity.() -> Boolean): SendLetterEntity? {
        return sendLetters.find { query(it) }
    }


    data class SendLetterEntity(
        val id: String,
        val receiverName: String,
        val content: String,
        val images: List<String>,
        val templateType: Int,
        val senderId: String,
        val letterCode: String,
        var isExpired: Boolean,
        var receiverId: String,
        val createdAt: LocalDateTime
    ) {
        fun toSendLetter(): SendLetter {
            return SendLetter(
                id = DomainId(id),
                receiverName = receiverName,
                content = LetterContent(
                    content = content,
                    images = images,
                    templateType = templateType
                ),
                senderId = DomainId(senderId),
                letterCode = letterCode,
                createdAt = createdAt
            )
        }

        companion object {
            fun fromSendLetter(sendLetter: SendLetter): SendLetterEntity {
                return SendLetterEntity(
                    id = sendLetter.id.value,
                    receiverName = sendLetter.receiverName,
                    content = sendLetter.content.content,
                    images = sendLetter.content.images,
                    templateType = sendLetter.content.templateType,
                    senderId = sendLetter.senderId.value,
                    letterCode = sendLetter.letterCode,
                    isExpired = false,
                    receiverId = "",
                    createdAt = sendLetter.createdAt
                )
            }
        }

        fun expire(receiverId: String) {
            this.isExpired = true
            this.receiverId = receiverId
        }
    }
}