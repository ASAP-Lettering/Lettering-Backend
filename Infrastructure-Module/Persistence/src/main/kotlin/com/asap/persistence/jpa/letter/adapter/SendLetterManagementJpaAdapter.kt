package com.asap.persistence.jpa.letter.adapter

import com.asap.application.letter.exception.LetterException
import com.asap.application.letter.port.out.SendLetterManagementPort
import com.asap.common.event.EventPublisher
import com.asap.domain.common.DomainId
import com.asap.domain.letter.entity.SendLetter
import com.asap.domain.letter.enums.LetterStatus
import com.asap.persistence.jpa.letter.SendLetterMapper
import com.asap.persistence.jpa.letter.repository.*
import org.springframework.stereotype.Repository

@Repository
class SendLetterManagementJpaAdapter(
    private val sendLetterJpaRepository: SendLetterJpaRepository,
    private val eventPublisher: EventPublisher,
) : SendLetterManagementPort {
    override fun save(sendLetter: SendLetter) {
        sendLetterJpaRepository.save(SendLetterMapper.toSendLetterEntity(sendLetter))
        eventPublisher.publishAll(sendLetter.pullEvents())
    }

    override fun getLetterNotNull(letterId: DomainId): SendLetter =
        sendLetterJpaRepository.findActiveSendLetterById(letterId.value)?.let {
            SendLetterMapper.toSendLetter(it)
        } ?: throw LetterException.SendLetterNotFoundException()

    override fun getLetterByCodeNotNull(letterCode: String): SendLetter =
        sendLetterJpaRepository.findActiveSendLetterByCode(letterCode)?.let {
            SendLetterMapper.toSendLetter(it)
        } ?: throw LetterException.SendLetterNotFoundException()

    override fun getReadLetterNotNull(
        receiverId: DomainId,
        letterCode: String,
    ): SendLetter =
        sendLetterJpaRepository
            .findActiveSendLetterByCodeAndReceiverIdAndLetterStatus(
                code = letterCode,
                receiverId = receiverId.value,
                letterStatus = LetterStatus.READ,
            )?.let {
                SendLetterMapper.toSendLetter(it)
            } ?: throw LetterException.SendLetterNotFoundException()

    override fun getReadLetterNotNull(
        receiverId: DomainId,
        letterId: DomainId,
    ): SendLetter =
        sendLetterJpaRepository
            .findActiveSendLetterByIdAndReceiverIdAndLetterStatus(
                id = letterId.value,
                receiverId = receiverId.value,
                letterStatus = LetterStatus.READ,
            )?.let {
                SendLetterMapper.toSendLetter(it)
            } ?: throw LetterException.SendLetterNotFoundException()

    override fun verifiedLetter(
        receiverId: DomainId,
        letterCode: String,
    ): Boolean =
        sendLetterJpaRepository.existsByLetterCodeAndReceiverId(
            letterCode = letterCode,
            receiverId = receiverId.value,
        )

    override fun getAllBy(senderId: DomainId): List<SendLetter> =
        sendLetterJpaRepository
            .findAllActiveSendLetterBySenderId(senderId.value)
            .map { SendLetterMapper.toSendLetter(it) }

    override fun getAllBy(
        senderId: DomainId,
        letterIds: List<DomainId>,
    ): List<SendLetter> {
        val letterIdsValue = letterIds.map { it.value }
        return sendLetterJpaRepository
            .findAllActiveSendLetterBySenderIdAndLetterIds(senderId.value, letterIdsValue)
            .map { SendLetterMapper.toSendLetter(it) }
    }

    override fun getSendLetterBy(
        senderId: DomainId,
        letterId: DomainId,
    ): SendLetter =
        sendLetterJpaRepository
            .findActiveSendLetterByIdAndSenderId(
                senderId = senderId.value,
                letterId = letterId.value,
            )?.let { SendLetterMapper.toSendLetter(it) }
            ?: throw LetterException.SendLetterNotFoundException()

    override fun delete(sendLetter: SendLetter) {
        sendLetterJpaRepository.deleteBy(SendLetterMapper.toSendLetterEntity(sendLetter))
    }
}
