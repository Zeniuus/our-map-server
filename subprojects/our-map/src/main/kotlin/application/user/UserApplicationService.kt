package application.user

import domain.user.entity.User
import domain.user.service.UserService

class UserApplicationService(
    private val userService: UserService
) {
    fun signUp(
        nickname: String,
        email: String,
        password: String,
        instagramId: String?
    ): User {
        return userService.createUser(
            nickname = nickname,
            email = email,
            password = password,
            instagramId = instagramId
        )
    }
}