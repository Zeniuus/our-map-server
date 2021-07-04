package infra.persistence.repository

import domain.user.entity.User
import domain.user.repository.UserRepository
import java.sql.SQLException

class JpaUserRepository(
    private val entityManagerHolder: GlobalEntityManagerHolder
) : JpaEntityRepositoryBase<User>(User::class.java, entityManagerHolder), UserRepository {
    override fun findByNickname(nickname: String): User? {
        val em = entityManagerHolder.get()
        val query = em.createQuery("""
            SELECT u
            FROM User u
            WHERE u.nickname = :nickname
        """.trimIndent(), User::class.java)
        query.setParameter("nickname", nickname)
        val users = query.resultList
        if (users.size > 1) {
            throw SQLException("query result contains more than one row.")
        }
        return users.firstOrNull()
    }

    override fun findByEmail(email: String): User? {
        val em = entityManagerHolder.get()
        val query = em.createQuery("""
            SELECT u
            FROM User u
            WHERE u.email = :email
        """.trimIndent(), User::class.java)
        query.setParameter("email", email)
        val users = query.resultList
        if (users.size > 1) {
            throw SQLException("query result contains more than one row.")
        }
        return users.firstOrNull()
    }
}
