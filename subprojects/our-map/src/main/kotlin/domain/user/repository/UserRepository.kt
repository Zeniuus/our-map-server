package domain.user.repository

import domain.EntityRepository
import domain.user.entity.User

interface UserRepository : EntityRepository<User, String> {
    fun findByNickname(nickname: String): User?
}
