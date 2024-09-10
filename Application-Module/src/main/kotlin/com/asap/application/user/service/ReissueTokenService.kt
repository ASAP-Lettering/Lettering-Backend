package com.asap.application.user.service

import com.asap.application.user.exception.UserException
import com.asap.application.user.port.`in`.ReissueTokenUsecase
import com.asap.application.user.port.out.UserManagementPort
import com.asap.application.user.port.out.UserTokenConvertPort
import com.asap.application.user.port.out.UserTokenManagementPort
import com.asap.domain.common.DomainId
import com.asap.domain.user.entity.UserToken
import org.springframework.stereotype.Service

@Service
class ReissueTokenService(
    private val userTokenConvertPort: UserTokenConvertPort,
    private val userTokenManagementPort: UserTokenManagementPort,
    private val userManagementPort: UserManagementPort
) : ReissueTokenUsecase {


    override fun reissue(request: ReissueTokenUsecase.Command): ReissueTokenUsecase.Response {
        if(userTokenManagementPort.isExistsToken(request.refreshToken).not()) {
            throw UserException.UserPermissionDeniedException("존재하지 않는 토큰입니다.")
        }
        val userClaims = userTokenConvertPort.resolveRefreshToken(request.refreshToken)
        userTokenManagementPort.deleteUserToken(request.refreshToken)

        val user = userManagementPort.getUserNotNull(DomainId(userClaims.userId))

        val accessToken = userTokenConvertPort.generateAccessToken(user)
        val refreshToken = userTokenConvertPort.generateRefreshToken(user)
        userTokenManagementPort.saveUserToken(UserToken(token = refreshToken))
        return ReissueTokenUsecase.Response(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }
}