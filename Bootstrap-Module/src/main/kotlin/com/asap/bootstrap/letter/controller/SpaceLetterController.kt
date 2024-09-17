package com.asap.bootstrap.letter.controller

import com.asap.application.letter.port.`in`.MoveLetterUsecase
import com.asap.bootstrap.letter.api.SpaceLetterApi
import com.asap.bootstrap.letter.dto.MoveLetterToSpaceRequest
import org.springframework.web.bind.annotation.RestController

@RestController
class SpaceLetterController(
    private val moveLetterUsecase: MoveLetterUsecase
): SpaceLetterApi {
    override fun getSpaceLetters(
        page: Int,
        size: Int,
        spaceId: String
    ) {
        throw UnsupportedOperationException("Not implemented yet")
    }

    override fun moveLetterToSpace(
        letterId: String,
        request: MoveLetterToSpaceRequest,
        userId: String
    ) {
        moveLetterUsecase.moveToSpace(
            command = MoveLetterUsecase.Command.ToSpace(
                letterId = letterId,
                spaceId = request.spaceId,
                userId = userId
            )
        )
    }

    override fun moveLetterToIndependentLetter(
        letterId: String,
        userId: String
    ) {
        moveLetterUsecase.moveToIndependent(
            command = MoveLetterUsecase.Command.ToIndependent(
                letterId = letterId,
                userId = userId
            )
        )
    }
}