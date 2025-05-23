package com.asap.application.user.service

import com.asap.application.user.exception.UserException
import com.asap.application.user.port.`in`.DeleteUserUsecase
import com.asap.application.user.port.`in`.RegisterUserUsecase
import com.asap.application.user.port.`in`.UpdateUserUsecase
import com.asap.application.user.port.out.UserAuthManagementPort
import com.asap.application.user.port.out.UserManagementPort
import com.asap.application.user.port.out.UserTokenConvertPort
import com.asap.application.user.port.out.UserTokenManagementPort
import com.asap.domain.common.DomainId
import com.asap.domain.user.entity.User
import com.asap.domain.user.entity.UserAuth
import com.asap.domain.user.entity.UserToken
import com.asap.domain.user.vo.UserPermission
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserCommandService(
    private val userTokenConvertPort: UserTokenConvertPort,
    private val userAuthManagementPort: UserAuthManagementPort,
    private val userManagementPort: UserManagementPort,
    private val userTokenManagementPort: UserTokenManagementPort,
) : RegisterUserUsecase,
    DeleteUserUsecase,
    UpdateUserUsecase {
    override fun registerUser(command: RegisterUserUsecase.Command): RegisterUserUsecase.Response {
        if (!userTokenManagementPort.isExistsToken(command.registerToken)) {
            throw UserException.UserPermissionDeniedException("존재하지 않는 가입 토큰입니다.")
        }
        val userClaims = userTokenConvertPort.resolveRegisterToken(command.registerToken)
        if (userAuthManagementPort.isExistsUserAuth(userClaims.socialId, userClaims.socialLoginProvider)) {
            throw UserException.UserAlreadyRegisteredException()
        }
        val registerUser =
            User.create(
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
            UserAuth.create(
                userId = registerUser.id,
                socialId = userClaims.socialId,
                socialLoginProvider = userClaims.socialLoginProvider,
            )

        userManagementPort.save(registerUser)
        userAuthManagementPort.saveUserAuth(userAuth)

        val accessToken = userTokenConvertPort.generateAccessToken(registerUser)
        val refreshToken = userTokenConvertPort.generateRefreshToken(registerUser)

        userTokenManagementPort.saveUserToken(UserToken.create(token = refreshToken, userId = registerUser.id))

        return RegisterUserUsecase.Response(accessToken, refreshToken, registerUser.id.value)
    }

    override fun delete(command: DeleteUserUsecase.Command) {
        val user = userManagementPort.getUserNotNull(DomainId(command.userId))

        user.delete(command.reason)
        userManagementPort.save(user)

        val userAuth = userAuthManagementPort.getNotNull(user.id)

        userAuth.delete()
        userAuthManagementPort.saveUserAuth(userAuth)
    }

    override fun executeFor(command: UpdateUserUsecase.Command.Birthday) {
        userManagementPort
            .getUserNotNull(DomainId(command.userId))
            .apply {
                this.updateBirthday(command.birthday)
                userManagementPort.save(this)
            }
    }

    override fun executeFor(command: UpdateUserUsecase.Command.Onboarding) {
        userManagementPort
            .getUserNotNull(DomainId(command.userId))
            .apply {
                this.updateOnboarding()
                userManagementPort.save(this)
            }
    }
}
