package com.asap.bootstrap.letter.controller

import com.asap.application.letter.port.`in`.GenerateDraftKeyUsecase
import com.asap.application.letter.port.`in`.UpdateDraftLetterUsecase
import com.asap.bootstrap.letter.api.DraftLetterApi
import com.asap.bootstrap.letter.dto.GenerateDraftKeyResponse
import com.asap.bootstrap.letter.dto.UpdateDraftLetterRequest
import org.springframework.web.bind.annotation.RestController

@RestController
class DraftLetterController(
    private val generateDraftKeyUsecase: GenerateDraftKeyUsecase,
    private val updateDraftLetterUsecase: UpdateDraftLetterUsecase,
) : DraftLetterApi {
    override fun getDraftKey(userId: String): GenerateDraftKeyResponse {
        val response = generateDraftKeyUsecase.command(GenerateDraftKeyUsecase.Command(userId))
        return GenerateDraftKeyResponse(response.draftId)
    }

    override fun updateDraft(
        draftId: String,
        userId: String,
        request: UpdateDraftLetterRequest,
    ) {
        updateDraftLetterUsecase.command(
            UpdateDraftLetterUsecase.Command(
                draftId = draftId,
                userId = userId,
                content = request.content,
                receiverName = request.receiverName,
                images = request.images,
            ),
        )
    }

    override fun getAllDrafts(userId: String) {
        TODO("Not yet implemented")
    }

    override fun deleteDraft(
        draftId: String,
        userId: String,
    ) {
        TODO("Not yet implemented")
    }
}
