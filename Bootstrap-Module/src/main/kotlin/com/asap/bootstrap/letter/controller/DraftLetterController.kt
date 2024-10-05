package com.asap.bootstrap.letter.controller

import com.asap.application.letter.port.`in`.GenerateDraftKeyUsecase
import com.asap.bootstrap.letter.api.DraftLetterApi
import com.asap.bootstrap.letter.dto.GenerateDraftKeyResponse
import org.springframework.web.bind.annotation.RestController

@RestController
class DraftLetterController(
    private val generateDraftKeyUsecase: GenerateDraftKeyUsecase,
) : DraftLetterApi {
    override fun getDraftKey(userId: String): GenerateDraftKeyResponse {
        val response = generateDraftKeyUsecase.command(GenerateDraftKeyUsecase.Command(userId))
        return GenerateDraftKeyResponse(response.draftId)
    }

    override fun updateDraft(
        draftId: String,
        userId: String,
    ) {
        TODO("Not yet implemented")
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
