package com.asap.application.letter.service

import com.asap.application.letter.port.`in`.GenerateDraftKeyUsecase
import com.asap.application.letter.port.`in`.RemoveDraftLetterUsecase
import com.asap.application.letter.port.`in`.UpdateDraftLetterUsecase
import com.asap.application.letter.port.out.DraftLetterManagementPort
import com.asap.application.letter.port.out.ReceiveDraftLetterManagementPort
import com.asap.domain.common.DomainId
import com.asap.domain.letter.entity.DraftLetter
import com.asap.domain.letter.entity.ReceiveDraftLetter
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DraftLetterCommandService(
    private val draftLetterManagementPort: DraftLetterManagementPort,
    private val receiveDraftLetterManagementPort: ReceiveDraftLetterManagementPort,
) : GenerateDraftKeyUsecase,
    UpdateDraftLetterUsecase,
    RemoveDraftLetterUsecase {
    override fun command(command: GenerateDraftKeyUsecase.Command.Send): GenerateDraftKeyUsecase.Response {
        val draftLetter = DraftLetter.default(DomainId(command.userId))
        draftLetterManagementPort.save(draftLetter)
        return GenerateDraftKeyUsecase.Response(draftLetter.id.value)
    }

    override fun command(command: GenerateDraftKeyUsecase.Command.Physical): GenerateDraftKeyUsecase.Response {
        val receiveDraftLetter = ReceiveDraftLetter.default(DomainId(command.userId))
        receiveDraftLetterManagementPort.save(receiveDraftLetter)
        return GenerateDraftKeyUsecase.Response(receiveDraftLetter.id.value)
    }

    override fun command(command: UpdateDraftLetterUsecase.Command.Send) {
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

    override fun command(command: UpdateDraftLetterUsecase.Command.Physical) {
        val receiveDraftLetter =
            receiveDraftLetterManagementPort.getDraftLetterNotNull(
                draftId = DomainId(command.draftId),
                ownerId = DomainId(command.userId),
            )

        receiveDraftLetter.update(
            content = command.content,
            senderName = command.senderName,
            images = command.images,
        )
        receiveDraftLetterManagementPort.save(receiveDraftLetter)
    }
}
