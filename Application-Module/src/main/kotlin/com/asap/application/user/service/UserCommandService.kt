package com.asap.application.user.service

import com.asap.application.user.event.UserEvent
import com.asap.application.user.exception.UserException
import com.asap.application.user.port.`in`.DeleteUserUsecase
import com.asap.application.user.port.`in`.RegisterUserUsecase
import com.asap.application.user.port.out.UserAuthManagementPort
import com.asap.application.user.port.out.UserManagementPort
import com.asap.application.user.port.out.UserTokenConvertPort
import com.asap.application.user.port.out.UserTokenManagementPort
import com.asap.domain.common.DomainId
import com.asap.domain.user.entity.User
import com.asap.domain.user.entity.UserAuth
import com.asap.domain.user.entity.UserToken
import com.asap.domain.user.vo.UserPermission
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserCommandService(
    private val userTokenConvertPort: UserTokenConvertPort,
    private val userAuthManagementPort: UserAuthManagementPort,
    private val userManagementPort: UserManagementPort,
    private val userTokenManagementPort: UserTokenManagementPort,
    private val applicationEventPublisher: ApplicationEventPublisher,
) : RegisterUserUsecase,
    DeleteUserUsecase {
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

        val accessToken = userTokenConvertPort.generateAccessToken(registerUser)
        val refreshToken = userTokenConvertPort.generateRefreshToken(registerUser)

        userTokenManagementPort.saveUserToken(UserToken(token = refreshToken, userId = registerUser.id))

        // TODO: 이벤트 발행을 이렇게하는게 좋은지 다시 생각해보기
        applicationEventPublisher.publishEvent(UserEvent.UserCreatedEvent(registerUser))

        return RegisterUserUsecase.Response(accessToken, refreshToken)
    }

    override fun delete(command: DeleteUserUsecase.Command) {
        userManagementPort
            .getUserNotNull(DomainId(command.userId))
            .apply {
                this.delete()
                userManagementPort.saveUser(this)
                applicationEventPublisher.publishEvent(UserEvent.UserDeletedEvent(this))
            }.also {
                userAuthManagementPort.getNotNull(it.id).apply {
                    this.delete()
                    userAuthManagementPort.saveUserAuth(this)
                }
            }
    }
}
