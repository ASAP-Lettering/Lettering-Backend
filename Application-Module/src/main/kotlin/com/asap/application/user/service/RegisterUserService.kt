package com.asap.application.user.service

import com.asap.application.user.event.UserEvent
import com.asap.application.user.exception.UserException
import com.asap.application.user.port.`in`.RegisterUserUsecase
import com.asap.application.user.port.out.UserAuthManagementPort
import com.asap.application.user.port.out.UserManagementPort
import com.asap.application.user.port.out.UserTokenConvertPort
import com.asap.application.user.port.out.UserTokenManagementPort
import com.asap.domain.user.entity.User
import com.asap.domain.user.entity.UserAuth
import com.asap.domain.user.entity.UserToken
import com.asap.domain.user.vo.UserPermission
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class RegisterUserService(
    private val userTokenConvertPort: UserTokenConvertPort,
    private val userAuthManagementPort: UserAuthManagementPort,
    private val userManagementPort: UserManagementPort,
    private val userTokenManagementPort: UserTokenManagementPort,
    private val applicationEventPublisher: ApplicationEventPublisher,
) : RegisterUserUsecase {
    /**
     * 1. register token으로부터 사용자 정보 추출 -> 토큰이 이미 사용됐으면 에러
     * 2. 추출한 사용자가 이미 존재하는지 확인 -> 이미 존재하면 에러
     * 3. 추출한 사용자 정보와 함께 사용자 동의 검증 -> 동의하지 않으면 에러
     * 4. 사용자 정보 저장 및 jwt 토큰 반환
     */
    override fun registerUser(command: RegisterUserUsecase.Command): RegisterUserUsecase.Response {
        if (!userTokenManagementPort.isExistsToken(command.registerToken)) {
            throw UserException.UserPermissionDeniedException("존재하지 않는 가입 토큰입니다.")
        }
        val userClaims = userTokenConvertPort.resolveRegisterToken(command.registerToken)
        if (userAuthManagementPort.isExistsUserAuth(userClaims.socialId, userClaims.socialLoginProvider)) {
            throw UserException.UserAlreadyRegisteredException()
        }
        val registerUser =
            User(
                username = command.realName,
                profileImage = userClaims.profileImage,
                permission =
                    UserPermission(
                        command.servicePermission,
                        command.privatePermission,
                        command.marketingPermission,
                    ),
                birthday = command.birthday,
                email = userClaims.email,
            )
        val userAuth =
            UserAuth(
                userId = registerUser.id,
                socialId = userClaims.socialId,
                socialLoginProvider = userClaims.socialLoginProvider,
            )

        userManagementPort.saveUser(registerUser)
        userAuthManagementPort.saveUserAuth(userAuth)

        // TODO 더 좋은 방법 없는지 고민해보기
        applicationEventPublisher.publishEvent(UserEvent.UserCreatedEvent(registerUser))

        val accessToken = userTokenConvertPort.generateAccessToken(registerUser)
        val refreshToken = userTokenConvertPort.generateRefreshToken(registerUser)

        userTokenManagementPort.saveUserToken(UserToken(token = refreshToken))

        applicationEventPublisher.publishEvent(UserEvent.UserCreatedEvent(registerUser))

        return RegisterUserUsecase.Response(accessToken, refreshToken)
    }
}
