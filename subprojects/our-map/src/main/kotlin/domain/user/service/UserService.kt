package domain.user.service

import domain.DomainException
import domain.user.entity.User
import domain.user.repository.UserRepository
import domain.util.Bcrypt
import domain.util.EntityIdRandomGenerator

class UserService(
    private val userRepository: UserRepository
) {
    data class CreateUserParams(
        val nickname: String,
        val email: String,
        val password: String,
        val instagramId: String?
    )

    fun createUser(params: CreateUserParams): User {
        if (userRepository.findByNickname(params.nickname) != null) {
            throw DomainException("${params.nickname}은 이미 사용 중인 닉네임입니다.")
        }

        if (userRepository.findByEmail(params.email) != null) {
            throw DomainException("${params.email}은 이미 사용 중인 이메일입니다.")
        }

        return userRepository.add(User(
            id = EntityIdRandomGenerator.generate(),
            email = params.email,
            nickname = params.nickname,
            encryptedPassword = Bcrypt.encrypt(params.password),
            instagramId = params.instagramId
        ))
    }
}
