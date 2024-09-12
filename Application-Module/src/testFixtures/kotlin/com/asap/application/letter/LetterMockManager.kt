package com.asap.application.letter

import com.asap.application.letter.port.out.SendLetterManagementPort
import com.asap.domain.common.DomainId
import com.asap.domain.letter.entity.SendLetter
import com.asap.domain.letter.service.LetterCodeGenerator

class LetterMockManager(
    private val sendLetterManagementPort: SendLetterManagementPort
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
}