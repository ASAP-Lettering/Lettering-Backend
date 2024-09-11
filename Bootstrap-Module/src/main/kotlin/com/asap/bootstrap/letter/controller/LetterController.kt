package com.asap.bootstrap.letter.controller

import com.asap.application.letter.port.`in`.SendLetterUsecase
import com.asap.bootstrap.letter.api.LetterApi
import com.asap.bootstrap.letter.dto.*
import org.springframework.web.bind.annotation.RestController

@RestController
class LetterController(
    private val sendLetterUsecase: SendLetterUsecase
) : LetterApi {
    override fun verifyLetter(request: LetterVerifyRequest): LetterVerifyResponse {
        TODO("Not yet implemented")
    }

    override fun getReceiveLetter(letterId: String): ReceiveLetterInfoResponse {
        TODO("Not yet implemented")
    }

    override fun addReceiveLetter(request: AddIndirectLetterRequest) {
        TODO("Not yet implemented")
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