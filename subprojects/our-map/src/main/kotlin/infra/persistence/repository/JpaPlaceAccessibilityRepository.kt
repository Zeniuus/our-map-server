package infra.persistence.repository

import domain.placeAccessibility.entity.PlaceAccessibility
import domain.placeAccessibility.repository.PlaceAccessibilityRepository

class JpaPlaceAccessibilityRepository(
    private val entityManagerHolder: GlobalEntityManagerHolder,
) : JpaEntityRepositoryBase<PlaceAccessibility>(PlaceAccessibility::class.java, entityManagerHolder), PlaceAccessibilityRepository {
    override fun findByPlaceIds(placeIds: Collection<String>): List<PlaceAccessibility> {
        val em = entityManagerHolder.get()
        val query = em.createQuery("""
            SELECT pa
            FROM PlaceAccessibility pa
            WHERE pa.placeId IN :placeIds
        """.trimIndent(), PlaceAccessibility::class.java)
        query.setParameter("placeIds", placeIds)
        return query.resultList
    }
}
