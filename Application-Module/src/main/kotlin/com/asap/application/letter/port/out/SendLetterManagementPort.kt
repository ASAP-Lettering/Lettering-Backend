package com.asap.application.letter.port.out

import com.asap.application.letter.exception.LetterException
import com.asap.domain.common.DomainId
import com.asap.domain.letter.entity.SendLetter

interface SendLetterManagementPort {

    fun save(
        sendLetter: SendLetter
    )

    @Throws(
        LetterException.SendLetterNotFoundException::class
    )
    fun getLetterNotNull(
        letterId: DomainId
    ): SendLetter

    @Throws(
        LetterException.SendLetterNotFoundException::class
    )
    fun getLetterByCodeNotNull(
        letterCode: String
    ): SendLetter

    @Throws(
        LetterException.SendLetterNotFoundException::class
    )
    fun getExpiredLetterNotNull(
        receiverId: DomainId,
        letterCode: String
    ): SendLetter


    fun expireLetter(
        receiverId: DomainId,
        letterId: DomainId
    )

    fun verifiedLetter(
        receiverId: DomainId,
        letterCode: String
    ): Boolean

}