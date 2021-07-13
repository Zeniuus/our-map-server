package domain.user.service

import com.auth0.jwt.exceptions.JWTVerificationException
import domain.user.entity.User
import domain.user.exception.UserAuthenticationException
import domain.user.repository.UserRepository
import domain.util.Bcrypt
import domain.util.JWT

class UserAuthService(
    private val jwt: JWT,
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
        return jwt.issueToken(UserAccessTokenPayload(
            userId = user.id,
        ))
    }

    fun verifyAccessToken(token: String): UserAccessTokenPayload {
        val payload = try {
            jwt.verify(token, UserAccessTokenPayload::class)
        } catch (e: JWTVerificationException) {
            throw UserAuthenticationException(UserAuthenticationException.ErrorCode.INVALID_ACCESS_TOKEN)
        }
        if (userRepository.findById(payload.userId) == null) {
            throw UserAuthenticationException(UserAuthenticationException.ErrorCode.INVALID_ACCESS_TOKEN)
        }
        return payload
    }
}
