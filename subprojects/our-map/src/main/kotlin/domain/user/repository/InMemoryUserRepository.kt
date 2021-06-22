package domain.user.repository

import domain.user.entity.User

class InMemoryUserRepository : UserRepository {
    private val usersById = mutableMapOf<String, User>()

    override fun add(user: User): User {
        usersById[user.id] = user
        return user
    }

    override fun removeAll() {
        usersById.clear()
    }

    override fun findById(id: String): User? {
        return usersById[id]
    }

    override fun findByNickname(nickname: String): User? {
        return usersById.values.find { it.nickname == nickname }
    }
}