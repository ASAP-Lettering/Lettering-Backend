package com.asap.persistence.jpa.letter.adapter

import com.asap.application.letter.exception.LetterException
import com.asap.application.letter.port.out.SendLetterManagementPort
import com.asap.domain.common.DomainId
import com.asap.domain.letter.entity.SendLetter
import com.asap.domain.letter.enums.LetterStatus
import com.asap.persistence.jpa.letter.SendLetterMapper
import com.asap.persistence.jpa.letter.repository.*
import org.springframework.stereotype.Repository

@Repository
class SendLetterManagementJpaAdapter(
    private val sendLetterJpaRepository: SendLetterJpaRepository,
) : SendLetterManagementPort {
    override fun save(sendLetter: SendLetter) {
        sendLetterJpaRepository.save(SendLetterMapper.toSendLetterEntity(sendLetter))
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

    override fun getAllSendLetter(senderId: DomainId): List<SendLetter> =
        sendLetterJpaRepository
            .findAllActiveSendLetterBySenderId(senderId.value)
            .map { SendLetterMapper.toSendLetter(it) }
}
