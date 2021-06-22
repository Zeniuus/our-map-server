package domain.user.repository

import domain.user.entity.User

interface UserRepository {
    fun add(user: User): User
    fun findById(id: String): User?
    fun findByNickname(nickname: String): User?
}
