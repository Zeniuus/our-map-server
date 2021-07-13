package application.user

import application.TransactionManager
import domain.user.service.UserAuthService

class UserAuthApplicationService(
    private val transactionManager: TransactionManager,
    private val userAuthService: UserAuthService,
) {
    // User ID를 반환한다.
    fun verify(accessToken: String): String = transactionManager.doInTransaction {
        userAuthService.verifyAccessToken(accessToken).userId
    }
}
