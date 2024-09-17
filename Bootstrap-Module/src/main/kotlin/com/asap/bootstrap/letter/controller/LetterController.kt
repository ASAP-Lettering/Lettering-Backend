package com.asap.bootstrap.letter.controller

import com.asap.application.letter.port.`in`.*
import com.asap.bootstrap.letter.api.LetterApi
import com.asap.bootstrap.letter.dto.*
import com.asap.common.page.SliceResponse
import org.springframework.web.bind.annotation.RestController

@RestController
class LetterController(
    private val sendLetterUsecase: SendLetterUsecase,
    private val verifyLetterAccessibleUsecase: VerifyLetterAccessibleUsecase,
    private val getVerifiedLetterUsecase: GetVerifiedLetterUsecase,
    private val addLetterUsecase: AddLetterUsecase,
    private val getIndependentLettersUsecase: GetIndependentLettersUsecase,
    private val getSpaceLetterDetailUsecase: GetSpaceLetterDetailUsecase
) : LetterApi {
    override fun verifyLetter(
        request: LetterVerifyRequest,
        userId: String
    ): LetterVerifyResponse {
        val response = verifyLetterAccessibleUsecase.verify(
            VerifyLetterAccessibleUsecase.Command(
                letterCode = request.letterCode,
                userId = userId
            )
        )
        return LetterVerifyResponse(
            letterId = response.letterId
        )
    }

    override fun getVerifiedLetter(
        letterId: String,
        userId: String
    ): VerifiedLetterInfoResponse {
        val response = getVerifiedLetterUsecase.get(
            GetVerifiedLetterUsecase.Query(
                userId = userId,
                letterId = letterId
            )
        )
        return VerifiedLetterInfoResponse(
            senderName = response.senderName,
            content = response.content,
            date = response.sendDate,
            templateType = response.templateType,
            images = response.images
        )
    }

    override fun addVerifiedLetter(
        request: AddVerifiedLetterRequest,
        userId: String
    ) {
        addLetterUsecase.addVerifiedLetter(
            AddLetterUsecase.Command.VerifyLetter(
                letterId = request.letterId,
                userId = userId
            )
        )
    }

    override fun addPhysicalLetter(
        request: AddPhysicalLetterRequest,
        userId: String
    ) {
        addLetterUsecase.addPhysicalLetter(
            AddLetterUsecase.Command.AddPhysicalLetter(
                senderName = request.senderName,
                content = request.content,
                images = request.images,
                templateType = request.templateType,
                userId = userId
            )
        )
    }

    override fun getIndependentLetters(
        userId: String
    ): SliceResponse<GetIndependentLetterSimpleInfo> {
        val response = getIndependentLettersUsecase.get(
            GetIndependentLettersUsecase.Query(
                userId = userId
            )
        )
        return SliceResponse.of(
            content = response.letters.map {
                GetIndependentLetterSimpleInfo(
                    letterId = it.letterId,
                    senderName = it.senderName,
                    isNew = it.isNew
                )
            },
            size = response.letters.size,
            number = 0,
            hasNext = false
        )
    }

    override fun getLetterDetail(
        letterId: String,
        userId: String
    ): GetLetterDetailResponse {
        val response = getSpaceLetterDetailUsecase.get(
            GetSpaceLetterDetailUsecase.Query(
                letterId = letterId,
                userId = userId
            )
        )
        return GetLetterDetailResponse(
            senderName = response.senderName,
            spaceName = response.spaceName,
            letterCount = response.letterCount,
            content = response.content,
            sendDate = response.sendDate,
            images = response.images,
            templateType = response.templateType,
            prevLetter = response.prevLetter?.let {
                GetLetterDetailResponse.NearbyLetter(
                    letterId = it.letterId,
                    senderName = it.senderName
                )
            },
            nextLetter = response.nextLetter?.let {
                GetLetterDetailResponse.NearbyLetter(
                    letterId = it.letterId,
                    senderName = it.senderName
                )
            }
        )
    }

    override fun updateLetter(letterId: String, request: ModifyLetterRequest, userId: String) {
        TODO("Not yet implemented")
    }

    override fun deleteLetter(letterId: String, userId: String) {
        TODO("Not yet implemented")
    }


    override fun sendLetter(request: SendLetterRequest, userId: String): SendLetterResponse {
        val response = sendLetterUsecase.send(
            SendLetterUsecase.Command(
                receiverName = request.receiverName,
                content = request.content,
                images = request.images,
                templateType = request.templateType,
                draftId = request.draftId,
                userId = userId
            )
        )
        return SendLetterResponse(
            letterCode = response.letterCode
        )
    }
}