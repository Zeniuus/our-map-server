package infra.persistence.repository

import domain.accessibility.entity.BuildingAccessibilityComment
import domain.accessibility.repository.BuildingAccessibilityCommentRepository
import infra.persistence.transaction.EntityManagerHolder

class JpaBuildingAccessibilityCommentRepository :
    JpaEntityRepositoryBase<BuildingAccessibilityComment, String>(BuildingAccessibilityComment::class.java),
    BuildingAccessibilityCommentRepository {
    override fun findByBuildingId(buildingId: String): List<BuildingAccessibilityComment> {
        val em = EntityManagerHolder.get()!!
        val query = em.createQuery("""
            SELECT c
            FROM BuildingAccessibilityComment c
            WHERE c.buildingId = :buildingId
            ORDER BY c.createdAt DESC
        """.trimIndent(), BuildingAccessibilityComment::class.java)
        query.setParameter("buildingId", buildingId)
        return query.resultList
    }
}
