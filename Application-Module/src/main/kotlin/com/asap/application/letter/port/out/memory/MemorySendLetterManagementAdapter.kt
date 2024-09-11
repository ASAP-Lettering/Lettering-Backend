package com.asap.application.letter.port.out.memory

import com.asap.application.letter.port.out.SendLetterManagementPort
import com.asap.domain.letter.entity.SendLetter
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Component
@Primary
class MemorySendLetterManagementAdapter(

): SendLetterManagementPort {
    private val sendLetters = mutableListOf<SendLetter>()


    override fun save(sendLetter: SendLetter) {
        sendLetters.add(sendLetter)
    }
}