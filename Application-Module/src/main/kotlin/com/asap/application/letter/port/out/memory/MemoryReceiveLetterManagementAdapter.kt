package com.asap.application.letter.port.out.memory

import com.asap.application.letter.exception.LetterException
import com.asap.application.letter.port.out.IndependentLetterManagementPort
import com.asap.application.letter.port.out.SpaceLetterManagementPort
import com.asap.common.exception.DefaultException
import com.asap.domain.common.DomainId
import com.asap.domain.letter.entity.IndependentLetter
import com.asap.domain.letter.entity.SpaceLetter
import com.asap.domain.letter.vo.LetterContent
import com.asap.domain.letter.vo.ReceiverInfo
import com.asap.domain.letter.vo.SenderInfo
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class MemoryReceiveLetterManagementAdapter(

) : IndependentLetterManagementPort, SpaceLetterManagementPort {

    private val receiveLetters = mutableMapOf<String, ReceiveLetter>()


    override fun save(letter: IndependentLetter) {
        receiveLetters[letter.id.value] = fromDomain(letter)
    }

    override fun getAllByReceiverId(receiverId: DomainId): List<IndependentLetter> {
        return receiveLetters
            .filter { it.value.receiverId == receiverId.value }
            .filter { it.value.spaceId == null }
            .map {
                toIndependentLetterEntity(it.value)
            }
    }

    override fun getByIdNotNull(id: DomainId): IndependentLetter {
        return receiveLetters[id.value]?.let {
            toIndependentLetterEntity(it)
        } ?: throw LetterException.ReceiveLetterNotFoundException()
    }

    override fun save(letter: SpaceLetter) {
        receiveLetters[letter.id.value] = fromDomain(letter)
    }

    override fun saveByIndependentLetter(letter: IndependentLetter, spaceId: DomainId): SpaceLetter {
        val receiveLetter = receiveLetters[letter.id.value] ?: throw LetterException.ReceiveLetterNotFoundException()
        receiveLetter.spaceId = spaceId.value
        receiveLetter.movedAt = LocalDateTime.now()
        return toSpaceLetterEntity(receiveLetter)
    }


    data class ReceiveLetter(
        val id: String,
        val content: String,
        val images: List<String>,
        val templateType: Int,
        val senderId: String?,
        val senderName: String,
        val receiverId: String,
        val receiveDate: LocalDate,
        var spaceId: String?,
        val createdAt: LocalDateTime = LocalDateTime.now(),
        val updatedAt: LocalDateTime = LocalDateTime.now(),
        var movedAt: LocalDateTime = LocalDateTime.now()
    )


    companion object {
        fun fromDomain(letter: IndependentLetter): ReceiveLetter {
            return ReceiveLetter(
                id = letter.id.value,
                content = letter.content.content,
                images = letter.content.images,
                templateType = letter.content.templateType,
                senderId = letter.sender.senderId?.value,
                senderName = letter.sender.senderName,
                receiverId = letter.receiver.receiverId.value,
                receiveDate = letter.receiveDate,
                spaceId = null
            )
        }

        fun fromDomain(letter: SpaceLetter): ReceiveLetter {
            return ReceiveLetter(
                id = letter.id.value,
                content = letter.content.content,
                images = letter.content.images,
                templateType = letter.content.templateType,
                senderId = letter.sender.senderId?.value,
                senderName = letter.sender.senderName,
                receiverId = letter.receiver.receiverId.value,
                receiveDate = letter.receiveDate,
                spaceId = letter.spaceId.value
            )
        }

        fun toIndependentLetterEntity(receiveLetter: ReceiveLetter): IndependentLetter {
            return IndependentLetter(
                id = DomainId(receiveLetter.id),
                content = LetterContent(
                    content = receiveLetter.content,
                    images = receiveLetter.images,
                    templateType = receiveLetter.templateType
                ),
                sender = SenderInfo(
                    senderId = receiveLetter.senderId?.let { DomainId(it) },
                    senderName = receiveLetter.senderName
                ),
                receiver = ReceiverInfo(
                    receiverId = DomainId(receiveLetter.receiverId)
                ),
                receiveDate = receiveLetter.receiveDate,
                isNew = receiveLetter.movedAt.isAfter(LocalDateTime.now().minusDays(1))
            )
        }

        fun toSpaceLetterEntity(receiveLetter: ReceiveLetter): SpaceLetter {
            return SpaceLetter(
                id = DomainId(receiveLetter.id),
                content = LetterContent(
                    content = receiveLetter.content,
                    images = receiveLetter.images,
                    templateType = receiveLetter.templateType
                ),
                sender = SenderInfo(
                    senderId = receiveLetter.senderId?.let { DomainId(it) },
                    senderName = receiveLetter.senderName
                ),
                receiver = ReceiverInfo(
                    receiverId = DomainId(receiveLetter.receiverId)
                ),
                receiveDate = receiveLetter.receiveDate,
                spaceId = receiveLetter.spaceId?.let { DomainId(it) } ?: throw DefaultException.InvalidStateException()
            )
        }
    }


}