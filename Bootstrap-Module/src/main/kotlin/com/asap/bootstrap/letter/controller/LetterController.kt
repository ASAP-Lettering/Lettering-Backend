package com.asap.bootstrap.letter.controller

import com.asap.application.letter.port.`in`.AddLetterUsecase
import com.asap.application.letter.port.`in`.GetVerifiedLetterUsecase
import com.asap.application.letter.port.`in`.SendLetterUsecase
import com.asap.application.letter.port.`in`.VerifyLetterAccessibleUsecase
import com.asap.bootstrap.letter.api.LetterApi
import com.asap.bootstrap.letter.dto.*
import org.springframework.web.bind.annotation.RestController

@RestController
class LetterController(
    private val sendLetterUsecase: SendLetterUsecase,
    private val verifyLetterAccessibleUsecase: VerifyLetterAccessibleUsecase,
    private val getVerifiedLetterUsecase: GetVerifiedLetterUsecase,
    private val addLetterUsecase: AddLetterUsecase
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
        val response = getVerifiedLetterUsecase.receive(
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

    override fun addAnonymousLetter(request: AddDirectLetterRequest) {
        TODO("Not yet implemented")
    }

    override fun getIndependentLetters(page: Int, size: Int) {
        TODO("Not yet implemented")
    }

    override fun getLetter(letterId: String) {
        TODO("Not yet implemented")
    }

    override fun updateLetter(letterId: String) {
        TODO("Not yet implemented")
    }

    override fun deleteLetter(letterId: String) {
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