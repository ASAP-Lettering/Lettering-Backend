package com.asap.application.letter

import com.asap.application.letter.port.out.IndependentLetterManagementPort
import com.asap.application.letter.port.out.SendLetterManagementPort
import com.asap.domain.common.DomainId
import com.asap.domain.letter.entity.IndependentLetter
import com.asap.domain.letter.entity.SendLetter
import com.asap.domain.letter.service.LetterCodeGenerator
import java.time.LocalDate

class LetterMockManager(
    private val sendLetterManagementPort: SendLetterManagementPort,
    private val independentLetterManagementPort: IndependentLetterManagementPort
) {

    private val letterCodeGenerator = LetterCodeGenerator()

    fun generateMockSendLetter(
        receiverName: String,
    ): String{
        val sendLetter = SendLetter(
            receiverName = receiverName,
            content = "content",
            images = listOf("image1", "image2"),
            templateType = 1,
            senderId = DomainId.generate(),
            letterCode = letterCodeGenerator.generateCode(
                content = "content",
                ownerId = DomainId.generate().value
            )
        )
        sendLetterManagementPort.save(sendLetter)
        return sendLetter.letterCode
    }

    fun generateMockExpiredSendLetter(
        receiverName: String,
        receiverId: String,
        senderId: String = DomainId.generate().value
    ): Map<String, Any>{
        val sendLetter = SendLetter(
            receiverName = receiverName,
            content = "content",
            images = listOf("image1", "image2"),
            templateType = 1,
            senderId = DomainId(senderId),
            letterCode = letterCodeGenerator.generateCode(
                content = "content",
                ownerId = DomainId(senderId).value
            )
        )
        sendLetterManagementPort.save(sendLetter)
        sendLetterManagementPort.expireLetter(DomainId(receiverId), sendLetter.id)
        return mapOf(
            "letterCode" to sendLetter.letterCode,
            "letterId" to sendLetter.id.value
        )
    }

    fun isExistVerifiedLetter(
        letterId: String,
        userId: String
    ): Boolean{
        return try{
            sendLetterManagementPort.getExpiredLetterNotNull(DomainId(userId), DomainId(letterId))
            true
        }catch (e: Exception){
            return false
        }
    }

    fun generateMockIndependentLetter(
        senderId: String,
        receiverId: String
    ): Map<String, Any>{
        val independentLetter = IndependentLetter(
            senderId = DomainId(senderId),
            receiverId = DomainId(receiverId),
            content = "content",
            receiveDate = LocalDate.now(),
            templateType = 1,
            images = listOf("image1", "image2"),
            isNew = true
        )
        independentLetterManagementPort.save(independentLetter)
        return mapOf(
            "letterId" to independentLetter.id.value,
        )
    }
}