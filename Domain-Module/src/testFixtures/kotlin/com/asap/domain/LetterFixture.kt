package com.asap.domain

import com.asap.domain.common.DomainId
import com.asap.domain.letter.entity.SendLetter
import com.asap.domain.letter.enums.LetterStatus
import com.asap.domain.letter.vo.LetterContent

object LetterFixture {
    fun generateSendLetter(
        senderId: DomainId = DomainId.generate(),
        receiverName: String = "receiverName",
        letterCode: String = "letterCode",
        status: LetterStatus = LetterStatus.READ,
        receiverId: DomainId = DomainId.generate(),
    ): SendLetter =
        SendLetter.create(
            receiverName = receiverName,
            content =
                LetterContent(
                    content = "content",
                    templateType = 1,
                    images = mutableListOf("image1", "image2"),
                ),
            senderId = senderId,
            letterCode = letterCode,
            status = status,
            receiverId = receiverId,
        )
}
