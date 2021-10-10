package infra.persistence.repository

import infra.persistence.transaction.EntityManagerHolder
import quest.domain.entity.ClubQuest
import quest.domain.repository.ClubQuestRepository

class JpaClubQuestRepository :
    ClubQuestRepository,
    JpaEntityRepositoryBase<ClubQuest, String>(ClubQuest::class.java) {
    override fun listNotDeleted(): List<ClubQuest> {
        val em = EntityManagerHolder.get()!!
        val query = em.createQuery("""
            SELECT cq
            FROM ClubQuest cq
            WHERE cq.deletedAt IS NULL
            ORDER BY cq.createdAt DESC
        """.trimIndent(), ClubQuest::class.java)
        return query.resultList
    }
}
