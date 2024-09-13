package com.asap.application.letter.port.out.memory

import com.asap.application.letter.port.out.IndependentLetterManagementPort
import com.asap.domain.common.DomainId
import com.asap.domain.letter.entity.IndependentLetter
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class MemoryIndependentLetterManagementAdapter(

): IndependentLetterManagementPort {

    private val independentLetters = mutableListOf<IndependentLetterEntity>()

    override fun save(letter: IndependentLetter) {
        independentLetters.add(IndependentLetterEntity.fromDomain(letter))
    }

    override fun getAllByReceiverId(receiverId: DomainId): List<IndependentLetter> {
        return independentLetters.filter { it.receiverId == receiverId.value }.map { it.toDomain() }
    }


    data class IndependentLetterEntity(
        val id: String,
        val senderId: String,
        val receiverId: String,
        val content: String,
        val createdDate: LocalDate,
        val templateType: Int,
        val images: List<String>,
        val isNew: Boolean,
        val createdAt: LocalDateTime = LocalDateTime.now()
    ){
        fun toDomain(): IndependentLetter {
            return IndependentLetter(
                id = DomainId(id),
                senderId = DomainId(senderId),
                receiverId = DomainId(receiverId),
                content = content,
                receiveDate = createdDate,
                templateType = templateType,
                images = images,
                isNew = isNew
            )
        }

        companion object{
            fun fromDomain(letter: IndependentLetter): IndependentLetterEntity {
                return IndependentLetterEntity(
                    id = letter.id.value,
                    senderId = letter.senderId.value,
                    receiverId = letter.receiverId.value,
                    content = letter.content,
                    createdDate = letter.receiveDate,
                    templateType = letter.templateType,
                    images = letter.images,
                    isNew = letter.isNew
                )
            }
        }
    }
}