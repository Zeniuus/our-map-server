package application.user

import application.TransactionIsolationLevel
import application.TransactionManager
import domain.user.entity.User
import domain.user.repository.UserRepository
import domain.user.service.UserAuthService
import domain.user.service.UserService

class UserApplicationService(
    private val transactionManager: TransactionManager,
    private val userRepository: UserRepository,
    private val userService: UserService,
    private val userAuthService: UserAuthService,
) {
    fun signUp(
        nickname: String,
        password: String,
        instagramId: String?
    ): LoginResult = transactionManager.doInTransaction(TransactionIsolationLevel.SERIALIZABLE) {
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
    ): LoginResult = transactionManager.doInTransaction(TransactionIsolationLevel.SERIALIZABLE) {
        val user = userAuthService.authenticate(nickname, password)
        val accessToken = userAuthService.issueAccessToken(user)
        LoginResult(user, accessToken)
    }

    data class LoginResult(
        val user: User,
        val accessToken: String
    )

    fun updateUserInfo(
        userId: String,
        nickname: String,
        instagramId: String?
    ): User = transactionManager.doInTransaction(TransactionIsolationLevel.SERIALIZABLE) {
        val user = userRepository.findById(userId)
        userService.updateUserInfo(user, nickname, instagramId)
    }
}
