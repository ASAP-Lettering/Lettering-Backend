package com.asap.bootstrap.web.letter.controller

import com.asap.application.letter.port.`in`.*
import com.asap.bootstrap.web.letter.api.DraftLetterApi
import com.asap.bootstrap.web.letter.dto.*
import org.springframework.web.bind.annotation.RestController

@RestController
class DraftLetterController(
    private val generateDraftKeyUsecase: GenerateDraftKeyUsecase,
    private val updateDraftLetterUsecase: UpdateDraftLetterUsecase,
    private val getDraftLetterUsecase: GetDraftLetterUsecase,
    private val getPhysicalDraftLetterUsecase: GetPhysicalDraftLetterUsecase,
    private val removeDraftLetterUsecase: RemoveDraftLetterUsecase,
) : DraftLetterApi {
    override fun getDraftKey(userId: String): GenerateDraftKeyResponse {
        val response = generateDraftKeyUsecase.command(GenerateDraftKeyUsecase.Command.Send(userId))
        return GenerateDraftKeyResponse(response.draftId)
    }

    override fun getPhysicalDraftKey(userId: String): GenerateDraftKeyResponse {
        val response = generateDraftKeyUsecase.command(GenerateDraftKeyUsecase.Command.Physical(userId))
        return GenerateDraftKeyResponse(response.draftId)
    }

    override fun updateDraft(
        draftId: String,
        userId: String,
        request: UpdateDraftLetterRequest,
    ) {
        updateDraftLetterUsecase.command(
            UpdateDraftLetterUsecase.Command.Send(
                draftId = draftId,
                userId = userId,
                content = request.content,
                receiverName = request.receiverName,
                images = request.images,
            ),
        )
    }

    override fun updatePhysicalDraft(
        draftId: String,
        userId: String,
        request: UpdatePhysicalDraftLetterRequest
    ) {
        updateDraftLetterUsecase.command(
            UpdateDraftLetterUsecase.Command.Physical(
                draftId = draftId,
                userId = userId,
                content = request.content,
                images = request.images,
                senderName = request.senderName,
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

    override fun getAllPhysicalDrafts(userId: String): GetAllPhysicalDraftLetterResponse {
        val response = getPhysicalDraftLetterUsecase.getAll(GetPhysicalDraftLetterUsecase.Query.All(userId))
        return GetAllPhysicalDraftLetterResponse(
            drafts = response.drafts.map {
                PhysicalDraftLetterInfo(
                    draftKey = it.draftKey,
                    senderName = it.senderName,
                    content = it.content,
                    lastUpdated = it.lastUpdated,
                )
            }
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

    override fun getPhysicalDraftLetter(
        draftKey: String,
        userId: String
    ): GetPhysicalDraftLetterResponse {
        val response = getPhysicalDraftLetterUsecase.getByKey(GetPhysicalDraftLetterUsecase.Query.ByKey(draftKey, userId))
        return GetPhysicalDraftLetterResponse(
            draftKey = response.draftKey,
            senderName = response.senderName,
            content = response.content,
            images = response.images,
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
