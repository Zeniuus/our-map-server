package domain.user.service

import com.auth0.jwt.exceptions.JWTVerificationException
import domain.DomainException
import domain.user.entity.User
import domain.user.exception.UserAuthenticationException
import domain.user.repository.UserRepository
import domain.util.Bcrypt
import domain.util.JWT

class UserAuthService(
    private val jwt: JWT,
    private val userRepository: UserRepository
) {
    fun authenticate(nickname: String, password: String): User {
        val user = userRepository.findByNickname(nickname) ?: throw DomainException("잘못된 계정 아이디입니다.")
        if (!Bcrypt.verify(password, user.encryptedPassword)) {
            throw DomainException("잘못된 비밀번호입니다.")
        }
        return user
    }

    fun issueAccessToken(user: User): String {
        return jwt.issueToken(UserAccessTokenPayload(
            userId = user.id,
        ))
    }

    fun verifyAccessToken(token: String): UserAccessTokenPayload {
        return try {
            jwt.verify(token, UserAccessTokenPayload::class)
        } catch (e: JWTVerificationException) {
            throw UserAuthenticationException()
        }
    }
}
