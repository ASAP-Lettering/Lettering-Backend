package com.asap.application.letter.service

import com.asap.application.letter.port.`in`.GetVerifiedLetterUsecase
import com.asap.application.letter.port.out.SendLetterManagementPort
import com.asap.application.user.port.out.UserManagementPort
import com.asap.domain.common.DomainId
import org.springframework.stereotype.Service

@Service
class LetterQueryService(
    private val sendLetterManagementPort: SendLetterManagementPort,
    private val userManagementPort: UserManagementPort
) : GetVerifiedLetterUsecase {

    override fun receive(query: GetVerifiedLetterUsecase.Query): GetVerifiedLetterUsecase.Response {
        sendLetterManagementPort.getExpiredLetterNotNull(
            receiverId = DomainId(query.userId),
            letterId = DomainId(query.letterId)
        ).let {
            val sender = userManagementPort.getUserNotNull(it.senderId)
            return GetVerifiedLetterUsecase.Response(
                senderName = sender.username,
                content = it.content,
                sendDate = it.createdDate,
                templateType = it.templateType,
                images = it.images
            )
        }
    }
}