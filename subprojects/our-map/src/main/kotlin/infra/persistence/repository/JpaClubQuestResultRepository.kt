package infra.persistence.repository

import infra.persistence.transaction.EntityManagerHolder
import quest.domain.entity.ClubQuestResult
import quest.domain.repository.ClubQuestResultRepository

class JpaClubQuestResultRepository :
    ClubQuestResultRepository,
    JpaEntityRepositoryBase<ClubQuestResult, String>(ClubQuestResult::class.java) {
    override fun findByQuestId(questId: String): List<ClubQuestResult> {
        val em = EntityManagerHolder.get()!!
        val query = em.createQuery("""
            SELECT cqr
            FROM ClubQuestResult cqr
            WHERE cqr.questId = :questId
        """.trimIndent(), ClubQuestResult::class.java)
        query.setParameter("questId", questId)
        return query.resultList
    }

    override fun listAll(): List<ClubQuestResult> {
        val em = EntityManagerHolder.get()!!
        val query = em.createQuery("""
            SELECT cqr
            FROM ClubQuestResult cqr
        """.trimIndent(), ClubQuestResult::class.java)
        return query.resultList
    }
}
