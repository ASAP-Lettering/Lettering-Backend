package com.asap.bootstrap.acceptance.letter

import com.asap.application.letter.port.`in`.*
import com.asap.bootstrap.AcceptanceSupporter
import org.springframework.boot.test.mock.mockito.MockBean

abstract class LetterAcceptanceSupporter : AcceptanceSupporter() {
    @MockBean
    lateinit var moveLetterUsecase: MoveLetterUsecase

    @MockBean
    lateinit var getSpaceLettersUsecase: GetSpaceLettersUsecase

    @MockBean
    lateinit var getSpaceLetterDetailUsecase: GetSpaceLetterDetailUsecase

    @MockBean
    lateinit var removeLetterUsecase: RemoveLetterUsecase

    @MockBean
    lateinit var verifyLetterAccessibleUsecase: VerifyLetterAccessibleUsecase

    @MockBean
    lateinit var sendLetterUsecase: SendLetterUsecase

    @MockBean
    lateinit var getVerifiedLetterUsecase: GetVerifiedLetterUsecase

    @MockBean
    lateinit var addLetterUsecase: AddLetterUsecase

    @MockBean
    lateinit var getIndependentLettersUsecase: GetIndependentLettersUsecase

    @MockBean
    lateinit var generateDraftKeyUsecase: GenerateDraftKeyUsecase

    @MockBean
    lateinit var updateDraftLetterUsecase: UpdateDraftLetterUsecase

    @MockBean
    lateinit var getDraftLetterUsecase: GetDraftLetterUsecase

    @MockBean
    lateinit var removeDraftLetterUsecase: RemoveDraftLetterUsecase

    @MockBean
    lateinit var updateLetterUsecase: UpdateLetterUsecase

    @MockBean
    lateinit var getAllLetterCountUsecase: GetAllLetterCountUsecase

    @MockBean
    lateinit var getSendLetterUsecase: GetSendLetterUsecase
}
