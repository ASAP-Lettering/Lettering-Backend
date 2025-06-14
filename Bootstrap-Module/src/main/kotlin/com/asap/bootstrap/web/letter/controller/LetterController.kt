package com.asap.bootstrap.web.letter.controller

import com.asap.application.letter.port.`in`.*
import com.asap.bootstrap.web.letter.api.LetterApi
import com.asap.bootstrap.web.letter.dto.*
import com.asap.common.page.ListResponse
import com.asap.common.page.SliceResponse
import org.springframework.web.bind.annotation.RestController

@RestController
class LetterController(
    private val sendLetterUsecase: SendLetterUsecase,
    private val verifyLetterAccessibleUsecase: VerifyLetterAccessibleUsecase,
    private val getVerifiedLetterUsecase: GetVerifiedLetterUsecase,
    private val addLetterUsecase: AddLetterUsecase,
    private val getIndependentLettersUsecase: GetIndependentLettersUsecase,
    private val removeLetterUsecase: RemoveLetterUsecase,
    private val updateLetterUsecase: UpdateLetterUsecase,
    private val getAllLetterCountUsecase: GetAllLetterCountUsecase,
    private val getSendLetterUsecase: GetSendLetterUsecase,
) : LetterApi {
    override fun verifyLetter(
        request: LetterVerifyRequest,
        userId: String,
    ): LetterVerifyResponse {
        val response =
            verifyLetterAccessibleUsecase.verify(
                VerifyLetterAccessibleUsecase.Command(
                    letterCode = request.letterCode,
                    userId = userId,
                ),
            )
        return LetterVerifyResponse(
            letterId = response.letterId,
        )
    }

    override fun getVerifiedLetter(
        letterId: String,
        userId: String,
    ): VerifiedLetterInfoResponse {
        val response =
            getVerifiedLetterUsecase.get(
                GetVerifiedLetterUsecase.Query(
                    userId = userId,
                    letterId = letterId,
                ),
            )
        return VerifiedLetterInfoResponse(
            senderName = response.senderName,
            content = response.content,
            date = response.sendDate,
            templateType = response.templateType,
            images = response.images,
        )
    }

    override fun addVerifiedLetter(
        request: AddVerifiedLetterRequest,
        userId: String,
    ) {
        addLetterUsecase.addVerifiedLetter(
            AddLetterUsecase.Command.VerifyLetter(
                letterId = request.letterId,
                userId = userId,
            ),
        )
    }

    override fun addPhysicalLetter(
        request: AddPhysicalLetterRequest,
        userId: String,
    ) {
        addLetterUsecase.addPhysicalLetter(
            AddLetterUsecase.Command.AddPhysicalLetter(
                senderName = request.senderName,
                content = request.content,
                images = request.images,
                templateType = request.templateType,
                userId = userId,
                draftId = request.draftId,
            ),
        )
    }

    override fun getIndependentLetters(userId: String): SliceResponse<GetIndependentLetterSimpleInfo> {
        val response =
            getIndependentLettersUsecase.getAll(
                GetIndependentLettersUsecase.QueryAll(
                    userId = userId,
                ),
            )
        return SliceResponse.of(
            content =
                response.letters.map {
                    GetIndependentLetterSimpleInfo(
                        letterId = it.letterId,
                        senderName = it.senderName,
                        isNew = it.isNew,
                    )
                },
            size = response.letters.size,
            number = 0,
            hasNext = false,
        )
    }

    override fun getIndependentLetterDetail(
        letterId: String,
        userId: String,
    ): GetIndependentLetterDetailResponse {
        val response =
            getIndependentLettersUsecase.get(
                GetIndependentLettersUsecase.Query(
                    userId = userId,
                    letterId = letterId,
                ),
            )
        return GetIndependentLetterDetailResponse(
            senderName = response.senderName,
            letterCount = response.letterCount,
            content = response.content,
            sendDate = response.sendDate,
            images = response.images,
            templateType = response.templateType,
            prevLetter =
                response.prevLetter?.let {
                    GetIndependentLetterDetailResponse.NearbyLetter(
                        letterId = it.letterId,
                        senderName = it.senderName,
                    )
                },
            nextLetter =
                response.nextLetter?.let {
                    GetIndependentLetterDetailResponse.NearbyLetter(
                        letterId = it.letterId,
                        senderName = it.senderName,
                    )
                },
        )
    }

    override fun deleteIndependentLetter(
        letterId: String,
        userId: String,
    ) {
        removeLetterUsecase.removeIndependentLetter(
            RemoveLetterUsecase.Command.IndependentLetter(
                letterId = letterId,
                userId = userId,
            ),
        )
    }

    override fun updateIndependentLetter(
        letterId: String,
        request: ModifyLetterRequest,
        userId: String,
    ) {
        updateLetterUsecase.updateIndependentLetter(
            UpdateLetterUsecase.Command.Independent(
                letterId = letterId,
                senderName = request.senderName,
                content = request.content,
                images = request.images,
                userId = userId,
                templateType = request.templateType,
            ),
        )
    }

    override fun sendLetter(
        request: SendLetterRequest,
        userId: String,
    ): SendLetterResponse {
        val response =
            sendLetterUsecase.send(
                SendLetterUsecase.Command(
                    receiverName = request.receiverName,
                    content = request.content,
                    images = request.images,
                    templateType = request.templateType,
                    draftId = request.draftId,
                    userId = userId,
                ),
            )
        return SendLetterResponse(
            letterCode = response.letterCode,
        )
    }

    override fun getLetterCount(userId: String): AllLetterCountResponse {
        val response =
            getAllLetterCountUsecase.get(
                GetAllLetterCountUsecase.Query(
                    userId = userId,
                ),
            )
        return AllLetterCountResponse(
            letterCount = response.letterCount,
            spaceCount = response.spaceCount,
        )
    }

    override fun getSendLetterHistory(userId: String): ListResponse<SendLetterHistoryResponse> {
        val response =
            getSendLetterUsecase.getHistory(
                GetSendLetterUsecase.Query.AllHistory(
                    userId = userId,
                ),
            )
        return ListResponse.of(
            content =
                response.map {
                    SendLetterHistoryResponse(
                        letterId = it.letterId,
                        receiverName = it.receiverName,
                        sendDate = it.sendDate,
                    )
                },
        )
    }

    override fun getSendLetterDetail(
        letterId: String,
        userId: String,
    ): SendLetterDetailResponse {
        val response =
            getSendLetterUsecase.getDetail(
                GetSendLetterUsecase.Query.Detail(
                    userId = userId,
                    letterId = letterId,
                ),
            )
        return SendLetterDetailResponse(
            receiverName = response.receiverName,
            sendDate = response.sendDate,
            content = response.content,
            images = response.images,
            templateType = response.templateType,
            letterCode = response.letterCode,
        )
    }

    override fun deleteSendLetter(
        letterId: String,
        userId: String,
    ) {
        removeLetterUsecase.removeSenderLetterBy(
            RemoveLetterUsecase.Command.SendLetter(
                letterId = letterId,
                userId = userId,
            ),
        )
    }

    override fun deleteSendLetters(
        request: DeleteSendLettersRequest,
        userId: String,
    ) {
        removeLetterUsecase.removeAllSenderLetterBy(
            RemoveLetterUsecase.Command.SendLetters(
                letterIds = request.letterIds,
                userId = userId,
            ),
        )
    }

    override fun sendAnonymousLetter(request: AnonymousSendLetterRequest): SendLetterResponse {
        val response =
            sendLetterUsecase.sendAnonymous(
                SendLetterUsecase.AnonymousCommand(
                    senderName = request.senderName,
                    receiverName = request.receiverName,
                    content = request.content,
                    images = request.images,
                    templateType = request.templateType,
                ),
            )
        return SendLetterResponse(
            letterCode = response.letterCode,
        )
    }
}
