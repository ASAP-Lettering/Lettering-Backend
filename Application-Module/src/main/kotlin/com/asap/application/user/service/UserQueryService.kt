package com.asap.application.user.service

import com.asap.application.user.port.`in`.GetUserInfoUsecase
import com.asap.application.user.port.out.UserAuthManagementPort
import com.asap.application.user.port.out.UserManagementPort
import com.asap.domain.common.DomainId
import org.springframework.stereotype.Service

@Service
class UserQueryService(
    private val userManagementPort: UserManagementPort,
    private val userAuthManagementPort: UserAuthManagementPort,
) : GetUserInfoUsecase {
    override fun getBy(query: GetUserInfoUsecase.Query.Me): GetUserInfoUsecase.Response {
        val user = userManagementPort.getUserNotNull(DomainId(query.userId))
        val userAuth = userAuthManagementPort.getNotNull(user.id)
        return GetUserInfoUsecase.Response(
            name = user.username,
            socialPlatform = userAuth.socialLoginProvider.name,
            email = user.email,
            birthday = user.birthday,
        )
    }
}
