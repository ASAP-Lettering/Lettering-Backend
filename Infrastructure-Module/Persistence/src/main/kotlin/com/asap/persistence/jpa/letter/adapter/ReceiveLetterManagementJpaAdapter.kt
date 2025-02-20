package com.asap.persistence.jpa.letter.adapter

import com.asap.application.letter.exception.LetterException
import com.asap.application.letter.port.out.IndependentLetterManagementPort
import com.asap.application.letter.port.out.SpaceLetterManagementPort
import com.asap.common.event.EventPublisher
import com.asap.common.page.Page
import com.asap.common.page.PageRequest
import com.asap.domain.common.DomainId
import com.asap.domain.letter.entity.IndependentLetter
import com.asap.domain.letter.entity.SpaceLetter
import com.asap.persistence.jpa.common.PageUtils.toJpaSort
import com.asap.persistence.jpa.letter.ReceiverLetterMapper
import com.asap.persistence.jpa.letter.entity.ReceiveLetterEntity
import com.asap.persistence.jpa.letter.repository.*
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class ReceiveLetterManagementJpaAdapter(
    private val receiveLetterJpaRepository: ReceiveLetterJpaRepository,
    private val eventPublisher: EventPublisher,
) : IndependentLetterManagementPort,
    SpaceLetterManagementPort {
    override fun save(letter: IndependentLetter) {
        ReceiveLetterEntity(
            id = letter.id.value,
            content = letter.content.content,
            templateType = letter.content.templateType,
            images = letter.content.images,
            senderId = letter.sender.senderId?.value,
            senderName = letter.sender.senderName,
            receiverId = letter.receiver.receiverId.value,
            receiveDate = letter.receiveDate,
            movedAt = letter.movedAt,
            isOpened = letter.isOpened,
            spaceId = null,
            createdAt = letter.createdAt,
            updatedAt = letter.updatedAt,
        ).also {
            receiveLetterJpaRepository.save(it)
            eventPublisher.publishAll(letter.pullEvents())
        }
    }

    override fun getAllByReceiverId(receiverId: DomainId): List<IndependentLetter> =
        receiveLetterJpaRepository
            .findAllIndependentLetterBy(receiverId.value)
            .map { ReceiverLetterMapper.toIndependentLetter(it) }

    override fun getIndependentLetterByIdNotNull(id: DomainId): IndependentLetter =
        receiveLetterJpaRepository
            .findIndependentById(id.value)
            ?.let { ReceiverLetterMapper.toIndependentLetter(it) }
            ?: throw LetterException.ReceiveLetterNotFoundException()

    override fun getIndependentLetterByIdNotNull(
        id: DomainId,
        userId: DomainId,
    ): IndependentLetter {
        val letter =
            receiveLetterJpaRepository.findIndependentByIdAndReceiverId(id.value, userId.value)
                ?: throw LetterException.ReceiveLetterNotFoundException()
        return ReceiverLetterMapper.toIndependentLetter(letter)
    }

    override fun countIndependentLetterByReceiverId(receiverId: DomainId): Long =
        receiveLetterJpaRepository.countActiveIndependentByReceiverId(receiverId.value)

    override fun getNearbyLetter(
        userId: DomainId,
        letterId: DomainId,
    ): Pair<IndependentLetter?, IndependentLetter?> {
        val letters =
            receiveLetterJpaRepository
                .findAllIndependentLetterBy(userId.value)
                .sortedBy { it.receiveDate }
        val index = letters.indexOfFirst { it.id == letterId.value }
        return Pair(
            letters.getOrNull(index - 1)?.let { ReceiverLetterMapper.toIndependentLetter(it) },
            letters.getOrNull(index + 1)?.let { ReceiverLetterMapper.toIndependentLetter(it) },
        )
    }

    override fun delete(letter: IndependentLetter) {
        receiveLetterJpaRepository.deleteByLetterId(letter.id.value)
    }

    override fun save(letter: SpaceLetter) {
        ReceiveLetterEntity(
            id = letter.id.value,
            content = letter.content.content,
            templateType = letter.content.templateType,
            images = letter.content.images,
            senderId = letter.sender.senderId?.value,
            senderName = letter.sender.senderName,
            receiverId = letter.receiver.receiverId.value,
            receiveDate = letter.receiveDate,
            spaceId = letter.spaceId.value,
            movedAt = LocalDateTime.now(),
            isOpened = false,
            createdAt = letter.createdAt,
            updatedAt = letter.updatedAt,
        ).apply {
            receiveLetterJpaRepository.save(this)
        }
    }

    override fun saveByIndependentLetter(
        letter: IndependentLetter,
        spaceId: DomainId,
        userId: DomainId,
    ): SpaceLetter {
        val receiveLetter =
            receiveLetterJpaRepository.findIndependentByIdAndReceiverId(letter.id.value, userId.value)
                ?: throw LetterException.ReceiveLetterNotFoundException()
        receiveLetter.spaceId = spaceId.value
        return ReceiverLetterMapper.toSpaceLetter(receiveLetter)
    }

    override fun getSpaceLetterNotNull(
        id: DomainId,
        userId: DomainId,
    ): SpaceLetter =
        receiveLetterJpaRepository
            .findSpaceByIdAndReceiverId(id.value, userId.value)
            ?.let { ReceiverLetterMapper.toSpaceLetter(it) }
            ?: throw LetterException.ReceiveLetterNotFoundException()

    override fun getNearbyLetter(
        spaceId: DomainId,
        userId: DomainId,
        letterId: DomainId,
    ): Pair<SpaceLetter?, SpaceLetter?> {
        val letters =
            receiveLetterJpaRepository
                .findAllActiveSpaceLetterBy(spaceId.value, userId.value)
                .sortedBy { it.receiveDate }
        val index = letters.indexOfFirst { it.id == letterId.value }
        return Pair(
            letters.getOrNull(index - 1)?.let { ReceiverLetterMapper.toSpaceLetter(it) },
            letters.getOrNull(index + 1)?.let { ReceiverLetterMapper.toSpaceLetter(it) },
        )
    }

    override fun countSpaceLetterBy(
        spaceId: DomainId,
        receiverId: DomainId,
    ): Long = receiveLetterJpaRepository.countActiveSpaceLetterBy(spaceId.value, receiverId.value)

    override fun countAllSpaceLetterBy(receiverId: DomainId): Long =
        receiveLetterJpaRepository.countAllActiveSpaceLetterBy(receiverId.value)

    override fun getAllBy(
        spaceId: DomainId,
        userId: DomainId,
        pageRequest: PageRequest,
    ): Page<SpaceLetter> {
        val letters =
            receiveLetterJpaRepository
                .findAllActiveSpaceLetterBy(
                    spaceId.value,
                    userId.value,
                    org.springframework.data.domain.PageRequest.of(
                        pageRequest.page,
                        pageRequest.size,
                        pageRequest.sorts.toJpaSort(),
                    ),
                ).map { ReceiverLetterMapper.toSpaceLetter(it) }
        return Page.of(
            content = letters.content,
            totalElements = letters.totalElements,
            totalPages = letters.totalPages,
            size = letters.size,
            page = pageRequest.page,
        )
    }

    override fun getAllBy(
        spaceId: DomainId,
        userId: DomainId,
    ): List<SpaceLetter> {
        val letters =
            receiveLetterJpaRepository
                .findAllActiveSpaceLetterBy(spaceId.value, userId.value)
                .map { ReceiverLetterMapper.toSpaceLetter(it) }
        return letters
    }

    override fun delete(letter: SpaceLetter) {
        receiveLetterJpaRepository.deleteByLetterId(letter.id.value)
    }
}
