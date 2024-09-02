package com.asap.application.user

import com.asap.application.user.port.out.UserTokenManagementPort
import com.asap.domain.user.entity.UserToken


class UserMockManager(
    private val tokenManagementPort: UserTokenManagementPort
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

}