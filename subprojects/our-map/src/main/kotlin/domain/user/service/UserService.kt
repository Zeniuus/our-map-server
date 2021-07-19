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
        val password: String,
        val instagramId: String?
    )

    fun createUser(params: CreateUserParams): User {
        val normalizedNickname = params.nickname.trim()
        if (normalizedNickname.length < 2) {
            throw DomainException("최소 2자 이상의 닉네임을 설정해주세요.")
        }
        if (userRepository.findByNickname(normalizedNickname) != null) {
            throw DomainException("${params.nickname}은 이미 사용 중인 닉네임입니다.")
        }

        return userRepository.add(
            User(
                id = EntityIdRandomGenerator.generate(),
                nickname = params.nickname,
                encryptedPassword = Bcrypt.encrypt(params.password),
                instagramId = params.instagramId
            )
        )
    }
}
