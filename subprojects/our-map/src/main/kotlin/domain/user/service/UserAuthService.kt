package domain.user.service

import domain.user.entity.User
import domain.user.exception.UserAuthenticationException
import domain.user.repository.UserRepository
import domain.util.Bcrypt

class UserAuthService(
    private val userRepository: UserRepository
) {
    fun authenticate(email: String, password: String): User {
        val user = userRepository.findByEmail(email) ?: throw UserAuthenticationException(UserAuthenticationException.ErrorCode.USER_DOES_NOT_EXIST)
        if (!Bcrypt.verify(password, user.encryptedPassword)) {
            throw UserAuthenticationException(UserAuthenticationException.ErrorCode.WRONG_PASSWORD)
        }
        return user
    }

    fun issueAccessToken(user: User): String {
        return "access-token" // TODO
    }
}
