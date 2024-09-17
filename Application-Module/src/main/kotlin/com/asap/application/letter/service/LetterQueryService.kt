package com.asap.application.letter.service

import com.asap.application.letter.port.`in`.GetIndependentLettersUsecase
import com.asap.application.letter.port.`in`.GetVerifiedLetterUsecase
import com.asap.application.letter.port.out.IndependentLetterManagementPort
import com.asap.application.letter.port.out.SendLetterManagementPort
import com.asap.application.user.port.out.UserManagementPort
import com.asap.domain.common.DomainId
import org.springframework.stereotype.Service

@Service
class LetterQueryService(
    private val sendLetterManagementPort: SendLetterManagementPort,
    private val userManagementPort: UserManagementPort,
    private val independentLetterManagementPort: IndependentLetterManagementPort
) : GetVerifiedLetterUsecase, GetIndependentLettersUsecase {

    override fun get(query: GetVerifiedLetterUsecase.Query): GetVerifiedLetterUsecase.Response {
        sendLetterManagementPort.getExpiredLetterNotNull(
            receiverId = DomainId(query.userId),
            letterId = DomainId(query.letterId)
        ).let {
            val sender = userManagementPort.getUserNotNull(it.senderId)
            return GetVerifiedLetterUsecase.Response(
                senderName = sender.username,
                content = it.content.content,
                sendDate = it.createdDate,
                templateType = it.content.templateType,
                images = it.content.images
            )
        }
    }

    override fun get(query: GetIndependentLettersUsecase.Query): GetIndependentLettersUsecase.Response {
        val letters = independentLetterManagementPort.getAllByReceiverId(DomainId(query.userId))
        return GetIndependentLettersUsecase.Response(
            letters = letters.map {
                GetIndependentLettersUsecase.LetterInfo(
                    letterId = it.id.value,
                    senderName = it.sender.senderName,
                    isNew = it.isNew
                )
            }
        )
    }
}