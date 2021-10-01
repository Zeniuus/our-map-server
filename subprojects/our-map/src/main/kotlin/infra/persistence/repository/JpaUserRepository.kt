package infra.persistence.repository

import domain.user.entity.User
import domain.user.repository.UserRepository
import infra.persistence.transaction.EntityManagerHolder

class JpaUserRepository :
    JpaEntityRepositoryBase<User, String>(User::class.java),
    UserRepository {
    override fun findByNickname(nickname: String): User? {
        val em = EntityManagerHolder.get()!!
        val query = em.createQuery("""
            SELECT u
            FROM User u
            WHERE u.nickname = :nickname
        """.trimIndent(), User::class.java)
        query.setParameter("nickname", nickname)
        return getSingularResultOrThrow(query.resultList)
    }

    override fun findByIdIn(ids: List<String>): List<User> {
        val em = EntityManagerHolder.get()!!
        val query = em.createQuery("""
            SELECT u
            FROM User u
            WHERE u.id in :ids
        """.trimIndent(), User::class.java)
        query.setParameter("ids", ids)
        return query.resultList
    }
}
