package domain.user.repository

import domain.InMemoryEntityRepositoryBase
import domain.user.entity.User

class InMemoryUserRepository : InMemoryEntityRepositoryBase<User, String>(), UserRepository {
    override fun findByNickname(nickname: String): User? {
        return entities.find { it.nickname == nickname }
    }

    override fun findByEmail(email: String): User? {
        return entities.find { it.email == email }
    }
}
