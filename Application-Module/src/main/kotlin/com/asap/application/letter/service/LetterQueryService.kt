package com.asap.application.letter.service

import com.asap.application.letter.port.`in`.*
import com.asap.application.letter.port.out.IndependentLetterManagementPort
import com.asap.application.letter.port.out.SendLetterManagementPort
import com.asap.application.letter.port.out.SpaceLetterManagementPort
import com.asap.application.space.port.out.SpaceManagementPort
import com.asap.application.user.port.out.UserManagementPort
import com.asap.common.page.PageRequest
import com.asap.common.page.Sort
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
    GetSpaceLetterDetailUsecase,
    GetAllLetterCountUsecase,
    GetSendLetterUsecase {
    override fun get(query: GetVerifiedLetterUsecase.Query): GetVerifiedLetterUsecase.Response {
        sendLetterManagementPort
            .getReadLetterNotNull(
                receiverId = DomainId(query.userId),
                letterId = DomainId(query.letterId),
            ).also {
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

    override fun getAll(queryAll: GetIndependentLettersUsecase.QueryAll): GetIndependentLettersUsecase.Response.All {
        val letters = independentLetterManagementPort.getAllByReceiverId(DomainId(queryAll.userId))
        return GetIndependentLettersUsecase.Response.All(
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

    @Transactional
    override fun get(query: GetIndependentLettersUsecase.Query): GetIndependentLettersUsecase.Response.One {
        val letter =
            independentLetterManagementPort
                .getIndependentLetterByIdNotNull(
                    DomainId(query.letterId),
                    DomainId(query.userId),
                ).apply {
                    read()
                    independentLetterManagementPort.save(this)
                }
        val letterCount = independentLetterManagementPort.countIndependentLetterByReceiverId(DomainId(query.userId))
        val (prevLetter, nextLetter) =
            independentLetterManagementPort.getNearbyLetter(DomainId(query.userId), DomainId(query.letterId))
        return GetIndependentLettersUsecase.Response.One(
            senderName = letter.sender.senderName,
            letterCount = letterCount.toLong(),
            content = letter.content.content,
            sendDate = letter.receiveDate,
            images = letter.content.images,
            templateType = letter.content.templateType,
            prevLetter =
                prevLetter?.let {
                    GetIndependentLettersUsecase.NearbyLetter(
                        letterId = it.id.value,
                        senderName = it.sender.senderName,
                    )
                },
            nextLetter =
                nextLetter?.let {
                    GetIndependentLettersUsecase.NearbyLetter(
                        letterId = it.id.value,
                        senderName = it.sender.senderName,
                    )
                },
        )
    }

    override fun get(query: GetSpaceLettersUsecase.Query): GetSpaceLettersUsecase.Response {
        val letters =
            spaceLetterManagementPort.getAllBy(
                spaceId = DomainId(query.spaceId),
                userId = DomainId(query.userId),
                pageRequest =
                    PageRequest(
                        page = query.page,
                        size = query.size,
                        sorts = Sort("movedAt", Sort.Direction.DESC),
                    ),
            )
        return GetSpaceLettersUsecase.Response(
            letters =
                letters.content.map {
                    GetSpaceLettersUsecase.LetterInfo(
                        senderName = it.sender.senderName,
                        letterId = it.id.value,
                        receivedDate = it.receiveDate,
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
                userId = spaceLetter.receiver.receiverId,
                spaceId = spaceLetter.spaceId,
            )
        val letterCount =
            spaceLetterManagementPort.countSpaceLetterBy(
                spaceId = spaceLetter.spaceId,
                receiverId = spaceLetter.receiver.receiverId,
            )
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
            receiveDate = spaceLetter.receiveDate,
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

    override fun get(query: GetAllLetterCountUsecase.Query): GetAllLetterCountUsecase.Response {
        val userId = DomainId(query.userId)
        val independentLetterCount = independentLetterManagementPort.countIndependentLetterByReceiverId(userId)
        val spaceLetterCount = spaceLetterManagementPort.countAllSpaceLetterBy(userId)
        return GetAllLetterCountUsecase.Response(
            letterCount = independentLetterCount + spaceLetterCount,
            spaceCount = spaceManagementPort.countByUserId(userId),
        )
    }

    override fun getHistory(query: GetSendLetterUsecase.Query.AllHistory): List<GetSendLetterUsecase.Response.History> =
        sendLetterManagementPort.getAllBy(DomainId(query.userId)).map {
            GetSendLetterUsecase.Response.History(
                letterId = it.id.value,
                receiverName = it.receiverName,
                sendDate = it.createdDate,
            )
        }

    override fun getDetail(query: GetSendLetterUsecase.Query.Detail): GetSendLetterUsecase.Response.Detail {
        val letter =
            sendLetterManagementPort.getSendLetterBy(
                letterId = DomainId(query.letterId),
                senderId = DomainId(query.userId),
            )
        return GetSendLetterUsecase.Response.Detail(
            receiverName = letter.receiverName,
            sendDate = letter.createdDate,
            content = letter.content.content,
            images = letter.content.images,
            templateType = letter.content.templateType,
        )
    }
}
