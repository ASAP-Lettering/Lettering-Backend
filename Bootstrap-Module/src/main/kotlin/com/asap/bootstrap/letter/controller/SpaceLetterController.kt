package com.asap.bootstrap.letter.controller

import com.asap.application.letter.port.`in`.*
import com.asap.bootstrap.letter.api.SpaceLetterApi
import com.asap.bootstrap.letter.dto.GetSpaceLetterDetailResponse
import com.asap.bootstrap.letter.dto.GetSpaceLettersResponse
import com.asap.bootstrap.letter.dto.ModifyLetterRequest
import com.asap.bootstrap.letter.dto.MoveLetterToSpaceRequest
import com.asap.common.page.PageResponse
import org.springframework.web.bind.annotation.RestController

@RestController
class SpaceLetterController(
    private val moveLetterUsecase: MoveLetterUsecase,
    private val getSpaceLettersUsecase: GetSpaceLettersUsecase,
    private val getSpaceLetterDetailUsecase: GetSpaceLetterDetailUsecase,
    private val removeLetterUsecase: RemoveLetterUsecase,
    private val updateLetterUsecase: UpdateLetterUsecase,
) : SpaceLetterApi {
    override fun getAllSpaceLetters(
        page: Int,
        size: Int,
        spaceId: String,
        userId: String,
    ): PageResponse<GetSpaceLettersResponse> {
        val response =
            getSpaceLettersUsecase.get(
                query =
                    GetSpaceLettersUsecase.Query(
                        page = page,
                        size = size,
                        spaceId = spaceId,
                        userId = userId,
                    ),
            )
        return PageResponse.of(
            content =
                response.letters.map {
                    GetSpaceLettersResponse(
                        senderName = it.senderName,
                        letterId = it.letterId,
                        receivedDate = it.receivedDate,
                    )
                },
            totalElements = response.total,
            totalPages = response.totalPages,
            size = response.size,
            page = response.page,
        )
    }

    override fun moveLetterToSpace(
        letterId: String,
        request: MoveLetterToSpaceRequest,
        userId: String,
    ) {
        moveLetterUsecase.moveToSpace(
            command =
                MoveLetterUsecase.Command.ToSpace(
                    letterId = letterId,
                    spaceId = request.spaceId,
                    userId = userId,
                ),
        )
    }

    override fun moveLetterToIndependentLetter(
        letterId: String,
        userId: String,
    ) {
        moveLetterUsecase.moveToIndependent(
            command =
                MoveLetterUsecase.Command.ToIndependent(
                    letterId = letterId,
                    userId = userId,
                ),
        )
    }

    override fun getSpaceLetterDetail(
        letterId: String,
        userId: String,
    ): GetSpaceLetterDetailResponse {
        val response =
            getSpaceLetterDetailUsecase.get(
                GetSpaceLetterDetailUsecase.Query(
                    letterId = letterId,
                    userId = userId,
                ),
            )
        return GetSpaceLetterDetailResponse(
            senderName = response.senderName,
            spaceName = response.spaceName,
            letterCount = response.letterCount,
            content = response.content,
            receiveDate = response.receiveDate,
            images = response.images,
            templateType = response.templateType,
            prevLetter =
                response.prevLetter?.let {
                    GetSpaceLetterDetailResponse.NearbyLetter(
                        letterId = it.letterId,
                        senderName = it.senderName,
                    )
                },
            nextLetter =
                response.nextLetter?.let {
                    GetSpaceLetterDetailResponse.NearbyLetter(
                        letterId = it.letterId,
                        senderName = it.senderName,
                    )
                },
        )
    }

    override fun deleteSpaceLetter(
        letterId: String,
        userId: String,
    ) {
        removeLetterUsecase.removeSpaceLetter(
            command =
                RemoveLetterUsecase.Command.SpaceLetter(
                    letterId = letterId,
                    userId = userId,
                ),
        )
    }

    override fun updateSpaceLetter(
        letterId: String,
        request: ModifyLetterRequest,
        userId: String,
    ) {
        updateLetterUsecase.updateSpaceLetter(
            command =
                UpdateLetterUsecase.Command.Space(
                    letterId = letterId,
                    senderName = request.senderName,
                    content = request.content,
                    images = request.images,
                    userId = userId,
                ),
        )
    }
}
