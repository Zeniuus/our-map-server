package application.user

import application.TransactionManager
import domain.user.entity.User
import domain.user.service.UserAuthService
import domain.user.service.UserService

class UserApplicationService(
    private val transactionManager: TransactionManager,
    private val userService: UserService,
    private val userAuthService: UserAuthService,
) {
    fun signUp(
        nickname: String,
        password: String,
        instagramId: String?
    ): LoginResult = transactionManager.doInTransaction {
        val user = userService.createUser(
            UserService.CreateUserParams(
                nickname = nickname,
                password = password,
                instagramId = instagramId,
            )
        )
        val accessToken = userAuthService.issueAccessToken(user)
        LoginResult(user, accessToken)
    }

    fun login(
        nickname: String,
        password: String
    ): LoginResult = transactionManager.doInTransaction {
        val user = userAuthService.authenticate(nickname, password)
        val accessToken = userAuthService.issueAccessToken(user)
        LoginResult(user, accessToken)
    }

    data class LoginResult(
        val user: User,
        val accessToken: String
    )
}
