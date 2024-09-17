package com.asap.bootstrap.letter.controller

import com.asap.application.letter.port.`in`.GetSpaceLettersUsecase
import com.asap.application.letter.port.`in`.MoveLetterUsecase
import com.asap.bootstrap.letter.api.SpaceLetterApi
import com.asap.bootstrap.letter.dto.GetSpaceLettersResponse
import com.asap.bootstrap.letter.dto.MoveLetterToSpaceRequest
import com.asap.common.page.PageResponse
import org.springframework.web.bind.annotation.RestController

@RestController
class SpaceLetterController(
    private val moveLetterUsecase: MoveLetterUsecase,
    private val getSpaceLettersUsecase: GetSpaceLettersUsecase
): SpaceLetterApi {
    override fun getAllSpaceLetters(
        page: Int,
        size: Int,
        spaceId: String,
        userId: String
    ): PageResponse<GetSpaceLettersResponse> {
        val response = getSpaceLettersUsecase.get(
            query = GetSpaceLettersUsecase.Query(
                page = page,
                size = size,
                spaceId = spaceId,
                userId = userId
            )
        )
        return PageResponse.of(
            content = response.letters.map {
                GetSpaceLettersResponse(
                    senderName = it.senderName,
                    letterId = it.letterId
                )
            },
            totalElements = response.total,
            totalPages = response.totalPages,
            size = response.size,
            page = response.page
        )
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