package com.asap.application.letter.port.out

import com.asap.application.letter.exception.LetterException
import com.asap.domain.common.DomainId
import com.asap.domain.letter.entity.SendLetter

interface SendLetterManagementPort {
    fun save(sendLetter: SendLetter)

    @Throws(
        LetterException.SendLetterNotFoundException::class,
    )
    fun getLetterNotNull(letterId: DomainId): SendLetter

    @Throws(
        LetterException.SendLetterNotFoundException::class,
    )
    fun getLetterByCodeNotNull(letterCode: String): SendLetter

    @Throws(
        LetterException.SendLetterNotFoundException::class,
    )
    fun getReadLetterNotNull(
        receiverId: DomainId,
        letterCode: String,
    ): SendLetter

    fun getReadLetterNotNull(
        receiverId: DomainId,
        letterId: DomainId,
    ): SendLetter

    fun verifiedLetter(
        receiverId: DomainId,
        letterCode: String,
    ): Boolean

    fun getAllBy(senderId: DomainId): List<SendLetter>

    fun delete(sendLetter: SendLetter)
}
