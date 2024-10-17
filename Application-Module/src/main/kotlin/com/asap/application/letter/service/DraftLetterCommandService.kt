package com.asap.application.letter.service

import com.asap.application.letter.port.`in`.GenerateDraftKeyUsecase
import com.asap.application.letter.port.`in`.RemoveDraftLetterUsecase
import com.asap.application.letter.port.`in`.UpdateDraftLetterUsecase
import com.asap.application.letter.port.out.DraftLetterManagementPort
import com.asap.domain.common.DomainId
import com.asap.domain.letter.entity.DraftLetter
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DraftLetterCommandService(
    private val draftLetterManagementPort: DraftLetterManagementPort,
) : GenerateDraftKeyUsecase,
    UpdateDraftLetterUsecase,
    RemoveDraftLetterUsecase {
    override fun command(command: GenerateDraftKeyUsecase.Command): GenerateDraftKeyUsecase.Response {
        val draftLetter = DraftLetter.default(DomainId(command.userId))
        draftLetterManagementPort.save(draftLetter)
        return GenerateDraftKeyUsecase.Response(draftLetter.id.value)
    }

    override fun command(command: UpdateDraftLetterUsecase.Command) {
        val draftLetter =
            draftLetterManagementPort.getDraftLetterNotNull(
                draftId = DomainId(command.draftId),
                ownerId = DomainId(command.userId),
            )

        draftLetter.update(
            content = command.content,
            receiverName = command.receiverName,
            images = command.images,
        )
        draftLetterManagementPort.save(draftLetter)
    }

    override fun deleteBy(command: RemoveDraftLetterUsecase.Command.Draft) {
        draftLetterManagementPort
            .getDraftLetterNotNull(
                draftId = DomainId(command.draftId),
                ownerId = DomainId(command.userId),
            ).let {
                draftLetterManagementPort.remove(it)
            }
    }

    override fun deleteBy(command: RemoveDraftLetterUsecase.Command.User) {
        draftLetterManagementPort
            .getAllDrafts(DomainId(command.userId))
            .forEach {
                draftLetterManagementPort.remove(it)
            }
    }
}
