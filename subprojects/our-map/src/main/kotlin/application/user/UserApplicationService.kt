package application.user

import domain.user.entity.User
import domain.user.service.UserAuthService
import domain.user.service.UserService

class UserApplicationService(
    private val userService: UserService,
    private val userAuthService: UserAuthService,
) {
    fun signUp(
        nickname: String,
        email: String,
        password: String,
        instagramId: String?
    ): LoginResult {
        val user = userService.createUser(
            UserService.CreateUserParams(
                nickname = nickname,
                email = email,
                password = password,
                instagramId = instagramId,
            )
        )
        val accessToken = userAuthService.issueAccessToken(user)
        return LoginResult(user, accessToken)
    }

    fun login(
        email: String,
        password: String
    ): LoginResult {
        val user = userAuthService.authenticate(email, password)
        val accessToken = userAuthService.issueAccessToken(user)
        return LoginResult(user, accessToken)
    }

    data class LoginResult(
        val user: User,
        val accessToken: String
    )
}
