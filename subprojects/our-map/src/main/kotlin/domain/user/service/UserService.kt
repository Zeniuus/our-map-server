package domain.user.service

import domain.DomainException
import domain.user.entity.User
import domain.user.repository.UserRepository
import domain.util.Bcrypt
import domain.util.EntityIdRandomGenerator

class UserService(
    private val userRepository: UserRepository
) {
    fun createUser(
        nickname: String,
        email: String,
        password: String,
        instagramId: String?
    ): User {
        if (userRepository.findByNickname(nickname) != null) {
            throw DomainException("${nickname}은 이미 사용 중인 닉네임입니다.")
        }

        if (userRepository.findByEmail(email) != null) {
            throw DomainException("${email}은 이미 사용 중인 이메일입니다.")
        }

        return userRepository.add(User(
            id = EntityIdRandomGenerator.generate(),
            email = email,
            nickname = nickname,
            encryptedPassword = Bcrypt.encrypt(password),
            instagramId = instagramId
        ))
    }
}