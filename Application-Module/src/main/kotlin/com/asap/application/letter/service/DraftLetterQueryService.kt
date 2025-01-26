package com.asap.application.letter.service

import com.asap.application.letter.port.`in`.GetDraftLetterUsecase
import com.asap.application.letter.port.`in`.GetPhysicalDraftLetterUsecase
import com.asap.application.letter.port.out.DraftLetterManagementPort
import com.asap.application.letter.port.out.ReceiveDraftLetterManagementPort
import com.asap.domain.common.DomainId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class DraftLetterQueryService(
    private val draftLetterManagementPort: DraftLetterManagementPort,
    private val receiveDraftLetterManagementPort: ReceiveDraftLetterManagementPort,
) : GetDraftLetterUsecase, GetPhysicalDraftLetterUsecase {
    override fun getAll(query: GetDraftLetterUsecase.Query.All): GetDraftLetterUsecase.Response.All =
        GetDraftLetterUsecase.Response.All(
            drafts =
                draftLetterManagementPort
                    .getAllDrafts(DomainId(query.userId))
                    .map {
                        GetDraftLetterUsecase.Response.ByKey(
                            draftKey = it.id.value,
                            receiverName = it.receiverName,
                            content = it.content,
                            images = it.images,
                            lastUpdated = it.lastUpdated,
                        )
                    },
        )

    override fun getByKey(query: GetDraftLetterUsecase.Query.ByKey): GetDraftLetterUsecase.Response.ByKey =
        draftLetterManagementPort
            .getDraftLetterNotNull(
                ownerId = DomainId(query.userId),
                draftId = DomainId(query.draftKey),
            ).let {
                GetDraftLetterUsecase.Response.ByKey(
                    draftKey = it.id.value,
                    receiverName = it.receiverName,
                    content = it.content,
                    images = it.images,
                    lastUpdated = it.lastUpdated,
                )
            }

    override fun count(query: GetDraftLetterUsecase.Query.All): GetDraftLetterUsecase.Response.Count {
        val count = draftLetterManagementPort.countDrafts(DomainId(query.userId))
        return GetDraftLetterUsecase.Response.Count(count)
    }

    override fun getAll(query: GetPhysicalDraftLetterUsecase.Query.All): GetPhysicalDraftLetterUsecase.Response.All {
        val drafts = receiveDraftLetterManagementPort.getAllDrafts(DomainId(query.userId))
        return GetPhysicalDraftLetterUsecase.Response.All(
            drafts = drafts.map {
                GetPhysicalDraftLetterUsecase.Response.ByKey(
                    draftKey = it.id.value,
                    content = it.content,
                    images = it.images,
                    senderName = it.senderName,
                    lastUpdated = it.lastUpdated
                )
            }
        )
    }

    override fun getByKey(query: GetPhysicalDraftLetterUsecase.Query.ByKey): GetPhysicalDraftLetterUsecase.Response.ByKey {
        val draft = receiveDraftLetterManagementPort.getDraftLetterNotNull(
            ownerId = DomainId(query.userId),
            draftId = DomainId(query.draftKey),
        )
        return GetPhysicalDraftLetterUsecase.Response.ByKey(
            draftKey = draft.id.value,
            senderName = draft.senderName,
            content = draft.content,
            images = draft.images,
            lastUpdated = draft.lastUpdated
        )
    }
}
