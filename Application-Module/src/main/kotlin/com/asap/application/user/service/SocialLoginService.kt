package com.asap.application.user.service

import com.asap.application.user.port.`in`.SocialLoginUsecase
import com.asap.application.user.port.out.AuthInfoRetrievePort
import com.asap.application.user.port.out.UserAuthManagementPort
import com.asap.application.user.port.out.UserManagementPort
import com.asap.application.user.port.out.UserTokenConvertPort
import com.asap.common.exception.DefaultException
import com.asap.domain.user.enums.SocialLoginProvider
import org.springframework.stereotype.Service


@Service
class SocialLoginService(
    private val userAuthManagementPort: UserAuthManagementPort,
    private val authInfoRetrievePort: AuthInfoRetrievePort,
    private val userTokenConvertPort: UserTokenConvertPort,
    private val userManagementPort: UserManagementPort
) : SocialLoginUsecase {

    override fun login(command: SocialLoginUsecase.Command): SocialLoginUsecase.Response {
        val authInfo =
            authInfoRetrievePort.getAuthInfo(SocialLoginProvider.parse(command.provider), command.accessToken)
        val userAuth = userAuthManagementPort.getUserAuth(authInfo.socialId, authInfo.socialLoginProvider)
        return userAuth?.let {
            userManagementPort.getUser(userAuth.userId)?.let {
                SocialLoginUsecase.Success(
                    userTokenConvertPort.generateAccessToken(it),
                    userTokenConvertPort.generateRefreshToken(it)
                )
            } ?: run {
                throw DefaultException.InvalidStateException("사용자 인증정보만 존재합니다. - ${userAuth.userId}")
            }
        } ?: run {
            val registerToken = userTokenConvertPort.generateRegisterToken(
                authInfo.socialId,
                authInfo.socialLoginProvider.name,
                authInfo.username,
                authInfo.profileImage
            )

            SocialLoginUsecase.NonRegistered(registerToken)
        }
    }

}