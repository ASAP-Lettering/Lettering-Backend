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
}