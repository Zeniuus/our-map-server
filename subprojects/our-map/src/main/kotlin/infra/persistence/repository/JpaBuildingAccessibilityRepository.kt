package infra.persistence.repository

import domain.placeAccessibility.entity.BuildingAccessibility
import domain.placeAccessibility.repository.BuildingAccessibilityRepository
import infra.persistence.transaction.EntityManagerHolder

class JpaBuildingAccessibilityRepository :
    JpaEntityRepositoryBase<BuildingAccessibility, String>(BuildingAccessibility::class.java),
    BuildingAccessibilityRepository {
    override fun findByBuildingIds(buildingIds: Collection<String>): List<BuildingAccessibility> {
        val em = EntityManagerHolder.get()!!
        val query = em.createQuery("""
            SELECT ba
            FROM BuildingAccessibility ba
            WHERE ba.buildingId IN :buildingIds
        """.trimIndent(), BuildingAccessibility::class.java)
        query.setParameter("buildingIds", buildingIds)
        return query.resultList
    }
}
