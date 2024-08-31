package com.asap.application.user

import com.asap.application.user.port.out.UserTokenManagementPort
import com.asap.domain.user.entity.UserToken
import com.asap.domain.user.enums.TokenType


class UserMockManager(
    private val tokenManagementPort: UserTokenManagementPort
) {


    fun settingToken(
        token: String
    ){
        tokenManagementPort.saveUserToken(
            UserToken(
                token = token,
                type = TokenType.ACCESS
            )
        )
    }

}