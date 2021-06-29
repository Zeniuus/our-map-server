package domain.user.repository

import domain.user.entity.User

interface UserRepository {
    fun add(user: User): User
    fun removeAll()
    fun findById(id: String): User?
    fun findByNickname(nickname: String): User?
    fun findByEmail(email: String): User?
}
