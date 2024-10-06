package com.asap.application.letter.service

import com.asap.application.letter.port.`in`.GetIndependentLettersUsecase
import com.asap.application.letter.port.`in`.GetSpaceLetterDetailUsecase
import com.asap.application.letter.port.`in`.GetSpaceLettersUsecase
import com.asap.application.letter.port.`in`.GetVerifiedLetterUsecase
import com.asap.application.letter.port.out.IndependentLetterManagementPort
import com.asap.application.letter.port.out.SendLetterManagementPort
import com.asap.application.letter.port.out.SpaceLetterManagementPort
import com.asap.application.space.port.out.SpaceManagementPort
import com.asap.application.user.port.out.UserManagementPort
import com.asap.common.page.PageRequest
import com.asap.domain.common.DomainId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class LetterQueryService(
    private val sendLetterManagementPort: SendLetterManagementPort,
    private val userManagementPort: UserManagementPort,
    private val independentLetterManagementPort: IndependentLetterManagementPort,
    private val spaceLetterManagementPort: SpaceLetterManagementPort,
    private val spaceManagementPort: SpaceManagementPort,
) : GetVerifiedLetterUsecase,
    GetIndependentLettersUsecase,
    GetSpaceLettersUsecase,
    GetSpaceLetterDetailUsecase {
    override fun get(query: GetVerifiedLetterUsecase.Query): GetVerifiedLetterUsecase.Response {
        sendLetterManagementPort
            .getReadLetterNotNull(
                receiverId = DomainId(query.userId),
                letterId = DomainId(query.letterId),
            ).let {
                val sender = userManagementPort.getUserNotNull(it.senderId)
                return GetVerifiedLetterUsecase.Response(
                    senderName = sender.username,
                    content = it.content.content,
                    sendDate = it.createdDate,
                    templateType = it.content.templateType,
                    images = it.content.images,
                )
            }
    }

    override fun get(query: GetIndependentLettersUsecase.Query): GetIndependentLettersUsecase.Response {
        val letters = independentLetterManagementPort.getAllByReceiverId(DomainId(query.userId))
        return GetIndependentLettersUsecase.Response(
            letters =
                letters.map {
                    GetIndependentLettersUsecase.LetterInfo(
                        letterId = it.id.value,
                        senderName = it.sender.senderName,
                        isNew = it.isNew(),
                    )
                },
        )
    }

    override fun get(query: GetSpaceLettersUsecase.Query): GetSpaceLettersUsecase.Response {
        val letters =
            spaceLetterManagementPort.getAllBySpaceId(
                spaceId = DomainId(query.spaceId),
                userId = DomainId(query.userId),
                pageRequest =
                    PageRequest(
                        page = query.page,
                        size = query.size,
                    ),
            )
        return GetSpaceLettersUsecase.Response(
            letters =
                letters.content.map {
                    GetSpaceLettersUsecase.LetterInfo(
                        senderName = it.sender.senderName,
                        letterId = it.id.value,
                    )
                },
            total = letters.totalElements,
            page = letters.page,
            size = letters.size,
            totalPages = letters.totalPages,
        )
    }

    override fun get(query: GetSpaceLetterDetailUsecase.Query): GetSpaceLetterDetailUsecase.Response {
        val spaceLetter =
            spaceLetterManagementPort.getSpaceLetterNotNull(DomainId(query.letterId), DomainId(query.userId))
        val space =
            spaceManagementPort.getSpaceNotNull(
                spaceLetter.receiver.receiverId,
                spaceLetter.spaceId,
            )
        val letterCount = spaceLetterManagementPort.countLetterBySpaceId(spaceLetter.spaceId)
        val (prevLetter, nextLetter) =
            spaceLetterManagementPort.getNearbyLetter(
                spaceId = spaceLetter.spaceId,
                userId = spaceLetter.receiver.receiverId,
                letterId = spaceLetter.id,
            )
        return GetSpaceLetterDetailUsecase.Response(
            senderName = spaceLetter.sender.senderName,
            spaceName = space.name,
            letterCount = letterCount,
            content = spaceLetter.content.content,
            sendDate = spaceLetter.receiveDate,
            images = spaceLetter.content.images,
            templateType = spaceLetter.content.templateType,
            prevLetter =
                prevLetter?.let {
                    GetSpaceLetterDetailUsecase.NearbyLetter(
                        letterId = it.id.value,
                        senderName = it.sender.senderName,
                    )
                },
            nextLetter =
                nextLetter?.let {
                    GetSpaceLetterDetailUsecase.NearbyLetter(
                        letterId = it.id.value,
                        senderName = it.sender.senderName,
                    )
                },
        )
    }
}
