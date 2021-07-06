package infra.persistence.repository

import domain.placeAccessibility.entity.PlaceAccessibility
import domain.placeAccessibility.repository.PlaceAccessibilityRepository

class JpaPlaceAccessibilityRepository :
    JpaEntityRepositoryBase<PlaceAccessibility, String>(PlaceAccessibility::class.java),
    PlaceAccessibilityRepository {
    override fun findByPlaceIds(placeIds: Collection<String>): List<PlaceAccessibility> {
        val em = OurMapTransactionManager.getEntityManager()
        val query = em.createQuery("""
            SELECT pa
            FROM PlaceAccessibility pa
            WHERE pa.placeId IN :placeIds
        """.trimIndent(), PlaceAccessibility::class.java)
        query.setParameter("placeIds", placeIds)
        return query.resultList
    }
}