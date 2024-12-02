package com.asap.bootstrap.user.controller

import com.asap.application.user.port.`in`.*
import com.asap.bootstrap.user.api.UserApi
import com.asap.bootstrap.user.dto.*
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    private val registerUserUsecase: RegisterUserUsecase,
    private val logoutUsecase: LogoutUsecase,
    private val deleteUserUsecase: DeleteUserUsecase,
    private val getUserInfoUsecase: GetUserInfoUsecase,
    private val updateUserUsecase: UpdateUserUsecase,
) : UserApi {
    override fun registerUser(request: RegisterUserRequest): RegisterUserResponse {
        val response =
            registerUserUsecase.registerUser(
                RegisterUserUsecase.Command(
                    request.registerToken,
                    request.servicePermission,
                    request.privatePermission,
                    request.marketingPermission,
                    request.birthday,
                    request.realName,
                ),
            )
        return RegisterUserResponse(response.accessToken, response.refreshToken)
    }

    override fun logout(
        userId: String,
        request: LogoutRequest,
    ) {
        logoutUsecase.logout(
            LogoutUsecase.Command(
                userId = userId,
                refreshToken = request.refreshToken,
            ),
        )
    }

    override fun deleteUser(userId: String) {
        deleteUserUsecase.delete(
            DeleteUserUsecase.Command(
                userId = userId,
            ),
        )
    }

    override fun getRequestUserInfo(userId: String): UserInfoResponse {
        val response = getUserInfoUsecase.getBy(GetUserInfoUsecase.Query.Me(userId))
        return UserInfoResponse(
            name = response.name,
            socialPlatform = response.socialPlatform,
            email = response.email,
            birthday = response.birthday,
        )
    }

    override fun updateBirthday(
        userId: String,
        request: UpdateBirthdayRequest,
    ) {
        updateUserUsecase.executeFor(
            UpdateUserUsecase.Command.Birthday(
                userId = userId,
                birthday = request.birthday,
            ),
        )
    }

    override fun updateOnboarding(userId: String) {
        updateUserUsecase.executeFor(
            UpdateUserUsecase.Command.Onboarding(
                userId = userId,
            ),
        )
    }
}
