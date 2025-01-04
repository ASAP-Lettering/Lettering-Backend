package com.asap.bootstrap.web.letter.controller

import com.asap.application.letter.port.`in`.GenerateDraftKeyUsecase
import com.asap.application.letter.port.`in`.GetDraftLetterUsecase
import com.asap.application.letter.port.`in`.RemoveDraftLetterUsecase
import com.asap.application.letter.port.`in`.UpdateDraftLetterUsecase
import com.asap.bootstrap.web.letter.api.DraftLetterApi
import com.asap.bootstrap.web.letter.dto.*
import org.springframework.web.bind.annotation.RestController

@RestController
class DraftLetterController(
    private val generateDraftKeyUsecase: GenerateDraftKeyUsecase,
    private val updateDraftLetterUsecase: UpdateDraftLetterUsecase,
    private val getDraftLetterUsecase: GetDraftLetterUsecase,
    private val removeDraftLetterUsecase: RemoveDraftLetterUsecase,
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

    override fun getAllDrafts(userId: String): GetAllDraftLetterResponse =
        getDraftLetterUsecase
            .getAll(GetDraftLetterUsecase.Query.All(userId))
            .let {
                GetAllDraftLetterResponse(
                    drafts =
                        it.drafts.map { draft ->
                            DraftLetterInfo(
                                draftKey = draft.draftKey,
                                receiverName = draft.receiverName,
                                content = draft.content,
                                lastUpdated = draft.lastUpdated,
                            )
                        },
                )
            }

    override fun getDraftLetter(
        draftKey: String,
        userId: String,
    ): GetDraftLetterResponse =
        getDraftLetterUsecase
            .getByKey(
                GetDraftLetterUsecase.Query.ByKey(
                    userId = userId,
                    draftKey = draftKey,
                ),
            ).let {
                GetDraftLetterResponse(
                    draftKey = it.draftKey,
                    receiverName = it.receiverName,
                    content = it.content,
                    images = it.images,
                )
            }

    override fun getDraftCount(userId: String): GetDraftLetterCountResponse {
        val response = getDraftLetterUsecase.count(GetDraftLetterUsecase.Query.All(userId))
        return GetDraftLetterCountResponse(response.count)
    }

    override fun deleteDraft(
        draftId: String,
        userId: String,
    ) {
        removeDraftLetterUsecase.deleteBy(
            RemoveDraftLetterUsecase.Command.Draft(
                draftId = draftId,
                userId = userId,
            ),
        )
    }
}
