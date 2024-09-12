package com.asap.application.letter.port.out.memory

import com.asap.application.letter.port.out.IndependentLetterManagementPort
import com.asap.domain.letter.entity.IndependentLetter
import org.springframework.stereotype.Component

@Component
class MemoryIndependentLetterManagementAdapter(

): IndependentLetterManagementPort {

    private val independentLetters = mutableListOf<IndependentLetter>()

    override fun save(letter: IndependentLetter) {
        independentLetters.add(letter)
    }
}