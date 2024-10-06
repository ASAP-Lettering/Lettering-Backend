package com.asap.application.letter.service

import com.asap.application.letter.port.`in`.GenerateDraftKeyUsecase
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
    UpdateDraftLetterUsecase {
    override fun command(command: GenerateDraftKeyUsecase.Command): GenerateDraftKeyUsecase.Response {
        val draftLetter = DraftLetter.default(DomainId(command.userId))
        draftLetterManagementPort.save(draftLetter)
        return GenerateDraftKeyUsecase.Response(draftLetter.id.value)
    }

    override fun command(command: UpdateDraftLetterUsecase.Command) {
        val draftLetter =
            draftLetterManagementPort.getDraftLetterNotNull(
                DomainId(command.draftId),
                DomainId(command.userId),
            )

        draftLetter.update(
            content = command.content,
            receiverName = command.receiverName,
            images = command.images,
        )
        draftLetterManagementPort.update(draftLetter)
    }
}
