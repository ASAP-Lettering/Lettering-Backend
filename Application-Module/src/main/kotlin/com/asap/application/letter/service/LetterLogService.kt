package com.asap.application.letter.service

import com.asap.application.letter.port.`in`.LetterLogUsecase
import com.asap.application.letter.port.out.LetterLogManagementPort
import com.asap.application.letter.port.out.SendLetterManagementPort
import com.asap.domain.letter.entity.LetterLog
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class LetterLogService(
    private val sendLetterManagementPort: SendLetterManagementPort,
    private val letterLogManagementPort: LetterLogManagementPort
) : LetterLogUsecase {
    override fun log(request: LetterLogUsecase.LogRequest) {
        val letterCode = request.letterCode
        val letter = sendLetterManagementPort.getLetterByCodeNotNull(letterCode)

        LetterLog.create(
            targetLetterId = letter.id,
            logType = request.logType,
            content = request.logContent
        ).apply {
            letterLogManagementPort.save(this)
        }
    }

    override fun finLatestLogByLetterCode(letterCode: String): LetterLogUsecase.LogResponse? {
        val letter = sendLetterManagementPort.getLetterByCodeNotNull(letterCode)
        val latestLog = letterLogManagementPort.findLatestByLetterId(letter.id)

        return latestLog?.let {
            LetterLogUsecase.LogResponse(
                letterId = it.targetLetterId.value,
                logType = it.logType,
                logContent = it.content
            )
        }
    }
}