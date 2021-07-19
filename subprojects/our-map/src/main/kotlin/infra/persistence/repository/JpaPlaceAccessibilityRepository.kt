package infra.persistence.repository

import domain.placeAccessibility.entity.PlaceAccessibility
import domain.placeAccessibility.repository.PlaceAccessibilityRepository
import infra.persistence.transaction.EntityManagerHolder

class JpaPlaceAccessibilityRepository :
    JpaEntityRepositoryBase<PlaceAccessibility, String>(PlaceAccessibility::class.java),
    PlaceAccessibilityRepository {
    override fun findByPlaceIds(placeIds: Collection<String>): List<PlaceAccessibility> {
        val em = EntityManagerHolder.get()!!
        val query = em.createQuery("""
            SELECT pa
            FROM PlaceAccessibility pa
            WHERE pa.placeId IN :placeIds
        """.trimIndent(), PlaceAccessibility::class.java)
        query.setParameter("placeIds", placeIds)
        return query.resultList
    }

    override fun findByPlaceId(placeId: String): PlaceAccessibility? {
        val em = EntityManagerHolder.get()!!
        val query = em.createQuery("""
            SELECT pa
            FROM PlaceAccessibility pa
            WHERE pa.placeId = :placeId
        """.trimIndent(), PlaceAccessibility::class.java)
        query.setParameter("placeId", placeId)
        return getSingularResultOrThrow(query.resultList)
    }
}
