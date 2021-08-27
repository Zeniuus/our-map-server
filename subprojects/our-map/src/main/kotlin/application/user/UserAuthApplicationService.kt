package application.user

import application.TransactionManager
import domain.user.exception.UserAuthenticationException
import domain.user.repository.UserRepository
import domain.user.service.UserAuthService
import java.sql.SQLException

class UserAuthApplicationService(
    private val transactionManager: TransactionManager,
    private val userRepository: UserRepository,
    private val userAuthService: UserAuthService,
) {
    // User ID를 반환한다.
    fun verify(accessToken: String): String = transactionManager.doInTransaction {
        val userId = userAuthService.verifyAccessToken(accessToken).userId
        try {
            userRepository.findById(userId)
        } catch (e: SQLException) {
            throw UserAuthenticationException()
        }
        userId
    }
}
