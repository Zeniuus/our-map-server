package infra.persistence.repository

import domain.accessibility.entity.PlaceAccessibilityComment
import domain.accessibility.repository.PlaceAccessibilityCommentRepository
import infra.persistence.transaction.EntityManagerHolder

class JpaPlaceAccessibilityCommentRepository :
    JpaEntityRepositoryBase<PlaceAccessibilityComment, String>(PlaceAccessibilityComment::class.java),
    PlaceAccessibilityCommentRepository {
    override fun findByPlaceId(placeId: String): List<PlaceAccessibilityComment> {
        val em = EntityManagerHolder.get()!!
        val query = em.createQuery("""
            SELECT c
            FROM PlaceAccessibilityComment c
            WHERE c.placeId = :placeId
            ORDER BY c.createdAt DESC
        """.trimIndent(), PlaceAccessibilityComment::class.java)
        query.setParameter("placeId", placeId)
        return query.resultList
    }
}
