package infra.persistence.repository

import domain.placeAccessibility.entity.BuildingAccessibility
import domain.placeAccessibility.repository.BuildingAccessibilityRepository

class JpaBuildingAccessibililtyRepository :
    JpaEntityRepositoryBase<BuildingAccessibility, String>(BuildingAccessibility::class.java),
    BuildingAccessibilityRepository {
    override fun findByBuildingIds(buildingIds: Collection<String>): List<BuildingAccessibility> {
        val em = OurMapTransactionManager.getEntityManager()
        val query = em.createQuery("""
            SELECT ba
            FROM BuildingAccessibility ba
            WHERE ba.buildingId IN :buildingIds
        """.trimIndent(), BuildingAccessibility::class.java)
        query.setParameter("buildingIds", buildingIds)
        return query.resultList
    }
}
