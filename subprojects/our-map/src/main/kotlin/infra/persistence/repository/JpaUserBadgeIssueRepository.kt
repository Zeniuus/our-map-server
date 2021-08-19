package infra.persistence.repository

import domain.badge.entity.UserBadgeIssue
import domain.badge.repository.UserBadgeIssueRepository
import domain.user.entity.User
import infra.persistence.transaction.EntityManagerHolder

class JpaUserBadgeIssueRepository :
    JpaEntityRepositoryBase<UserBadgeIssue, String>(UserBadgeIssue::class.java),
    UserBadgeIssueRepository {
    override fun findByUser(user: User): List<UserBadgeIssue> {
        val em = EntityManagerHolder.get()!!
        val query = em.createQuery("""
            SELECT ubi
            FROM UserBadgeIssue ubi
            WHERE ubi.userId = :userId
        """.trimIndent(), UserBadgeIssue::class.java)
        query.setParameter("userId", user.id)
        return query.resultList
    }
}
