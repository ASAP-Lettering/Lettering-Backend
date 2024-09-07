package com.asap.application.user

import com.asap.application.user.port.out.UserManagementPort
import com.asap.application.user.port.out.UserTokenManagementPort
import com.asap.domain.common.DomainId
import com.asap.domain.user.entity.User
import com.asap.domain.user.entity.UserToken
import com.asap.domain.user.vo.UserPermission
import java.time.LocalDate


class UserMockManager(
    private val tokenManagementPort: UserTokenManagementPort,
    private val userManagementPort: UserManagementPort
) {


    fun settingToken(
        token: String
    ){
        tokenManagementPort.saveUserToken(
            UserToken(
                token = token,
            )
        )
    }

    fun settingUser(
        userId: String = "userId",
    ){
        userManagementPort.saveUser(
            User(
                id = DomainId(userId),
                username = "nickname",
                profileImage = "profileImage",
                permission = UserPermission(true, true, true),
                birthday = LocalDate.now()
            )
        )
    }

}