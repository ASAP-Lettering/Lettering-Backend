package com.asap.application.user.service

import com.asap.application.user.port.`in`.SocialLoginUsecase
import com.asap.application.user.port.out.*
import com.asap.common.exception.DefaultException
import com.asap.domain.user.entity.UserToken
import com.asap.domain.user.enums.SocialLoginProvider
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SocialLoginService(
    private val userAuthManagementPort: UserAuthManagementPort,
    private val authInfoRetrievePort: AuthInfoRetrievePort,
    private val userTokenConvertPort: UserTokenConvertPort,
    private val userManagementPort: UserManagementPort,
    private val userTokenManagementPort: UserTokenManagementPort,
) : SocialLoginUsecase {
    override fun login(command: SocialLoginUsecase.Command): SocialLoginUsecase.Response {
        val authInfo =
            authInfoRetrievePort.getAuthInfo(SocialLoginProvider.parse(command.provider), command.accessToken)
        val userAuth = userAuthManagementPort.getUserAuth(authInfo.socialId, authInfo.socialLoginProvider)
        return userAuth?.let {
            userManagementPort.getUser(userAuth.userId)?.let {
                val accessToken = userTokenConvertPort.generateAccessToken(it)
                val refreshToken = userTokenConvertPort.generateRefreshToken(it)
                userTokenManagementPort.saveUserToken(UserToken(token = refreshToken))
                SocialLoginUsecase.Success(accessToken, refreshToken)
            } ?: run {
                throw DefaultException.InvalidStateException("사용자 인증정보만 존재합니다. - ${userAuth.userId}")
            }
        } ?: run {
            val registerToken =
                userTokenConvertPort.generateRegisterToken(
                    socialId = authInfo.socialId,
                    socialLoginProvider = authInfo.socialLoginProvider.name,
                    username = authInfo.username,
                    profileImage = authInfo.profileImage,
                    email = authInfo.email,
                )
            userTokenManagementPort.saveUserToken(UserToken(token = registerToken))
            SocialLoginUsecase.NonRegistered(registerToken)
        }
    }
}
