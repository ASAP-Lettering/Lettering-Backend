package com.asap.application.letter.port.out

import com.asap.domain.letter.entity.SendLetter

interface SendLetterManagementPort {

    fun save(
        sendLetter: SendLetter
    )
}