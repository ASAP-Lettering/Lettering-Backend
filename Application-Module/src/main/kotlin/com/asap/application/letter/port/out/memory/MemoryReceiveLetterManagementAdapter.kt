package com.asap.application.letter.port.out.memory

import com.asap.application.letter.exception.LetterException
import com.asap.application.letter.port.out.IndependentLetterManagementPort
import com.asap.application.letter.port.out.SpaceLetterManagementPort
import com.asap.common.exception.DefaultException
import com.asap.common.page.Page
import com.asap.common.page.PageRequest
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
class MemoryReceiveLetterManagementAdapter : IndependentLetterManagementPort, SpaceLetterManagementPort {

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

    override fun getIndependentLetterByIdNotNull(id: DomainId): IndependentLetter {
        return receiveLetters[id.value]?.let {
            toIndependentLetterEntity(it)
        } ?: throw LetterException.ReceiveLetterNotFoundException()
    }

    override fun saveBySpaceLetter(letter: SpaceLetter, userId: DomainId): IndependentLetter {
        val receiveLetter = receiveLetters[letter.id.value] ?: throw LetterException.ReceiveLetterNotFoundException()
        if (receiveLetter.receiverId != userId.value) {
            throw DefaultException.InvalidStateException()
        }
        receiveLetter.spaceId = null
        receiveLetter.movedAt = LocalDateTime.now()
        return toIndependentLetterEntity(receiveLetter)
    }

    override fun save(letter: SpaceLetter) {
        receiveLetters[letter.id.value] = fromDomain(letter)
    }

    override fun saveByIndependentLetter(
        letter: IndependentLetter,
        spaceId: DomainId,
        userId: DomainId
    ): SpaceLetter {
        val receiveLetter = receiveLetters[letter.id.value] ?: throw LetterException.ReceiveLetterNotFoundException()
        if (receiveLetter.receiverId != userId.value) {
            throw DefaultException.InvalidStateException()
        }
        receiveLetter.spaceId = spaceId.value
        receiveLetter.movedAt = LocalDateTime.now()
        return toSpaceLetterEntity(receiveLetter)
    }

    override fun getSpaceLetterNotNull(id: DomainId): SpaceLetter {
        return receiveLetters[id.value]?.let {
            toSpaceLetterEntity(it)
        } ?: throw LetterException.ReceiveLetterNotFoundException()
    }

    override fun getSpaceLetterNotNull(id: DomainId, userId: DomainId): SpaceLetter {
        return receiveLetters[id.value]?.let {
            if (it.receiverId != userId.value) {
                throw DefaultException.InvalidStateException()
            }
            toSpaceLetterEntity(it)
        } ?: throw LetterException.ReceiveLetterNotFoundException()
    }

    override fun getNearbyLetter(
        spaceId: DomainId,
        userId: DomainId,
        letterId: DomainId
    ): Pair<SpaceLetter?, SpaceLetter?> {
        val letters = receiveLetters
            .filter { it.value.spaceId == spaceId.value }
            .filter { it.value.receiverId == userId.value }
            .keys
            .sortedBy { receiveLetters[it]!!.createdAt }
        val index = letters.indexOf(letterId.value)
        val prev = if (index > 0) {
            receiveLetters[letters[index - 1]]?.let {
                toSpaceLetterEntity(it)
            }
        } else {
            null
        }
        val next = if (index < letters.size - 1) {
            receiveLetters[letters[index + 1]]?.let {
                toSpaceLetterEntity(it)
            }
        } else {
            null
        }
        return Pair(prev, next)
    }

    override fun countLetterBySpaceId(spaceId: DomainId): Long {
        return receiveLetters
            .filter { it.value.spaceId == spaceId.value }
            .count()
            .toLong()
    }

    override fun getAllBySpaceId(spaceId: DomainId, userId: DomainId, pageRequest: PageRequest): Page<SpaceLetter> {
        val letters = receiveLetters
            .filter { it.value.spaceId == spaceId.value }
            .filter { it.value.receiverId == userId.value }
            .keys
            .sortedBy { receiveLetters[it]!!.createdAt }
            .subList(
                pageRequest.page * pageRequest.size,
                (pageRequest.page + 1) * pageRequest.size
            )
            .map {
                toSpaceLetterEntity(receiveLetters[it]!!)
            }
        val total = receiveLetters
            .filter { it.value.spaceId == spaceId.value }
            .filter { it.value.receiverId == userId.value }
            .count()
        return Page.of(
            content = letters,
            totalElements = total.toLong(),
            totalPages = total / pageRequest.size + 1,
            size = letters.size,
            page = pageRequest.page
        )
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