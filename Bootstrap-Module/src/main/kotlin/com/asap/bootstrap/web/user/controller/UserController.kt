package com.asap.bootstrap.web.user.controller

import com.asap.application.letter.port.`in`.AddLetterUsecase
import com.asap.application.user.port.`in`.*
import com.asap.bootstrap.web.user.api.UserApi
import com.asap.bootstrap.web.user.dto.*
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    private val registerUserUsecase: RegisterUserUsecase,
    private val logoutUsecase: LogoutUsecase,
    private val deleteUserUsecase: DeleteUserUsecase,
    private val getUserInfoUsecase: GetUserInfoUsecase,
    private val updateUserUsecase: UpdateUserUsecase,
    private val addLetterUsecase: AddLetterUsecase,
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

        // Handle anonymous letter code if it exists
        request.anonymousSendLetterCode?.let { letterCode ->
            addLetterUsecase.addAnonymousLetter(
                AddLetterUsecase.Command.AddAnonymousLetter(
                    letterCode = letterCode,
                    userId = response.userId,
                ),
            )
        }

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

    override fun deleteUser(
        userId: String,
        request: UnregisterUserRequest?,
    ) {
        deleteUserUsecase.delete(
            DeleteUserUsecase.Command(
                userId = userId,
                reason = request?.reason.orEmpty(),
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
