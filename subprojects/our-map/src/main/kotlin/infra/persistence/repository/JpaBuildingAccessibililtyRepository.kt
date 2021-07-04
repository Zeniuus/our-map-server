package infra.persistence.repository

import domain.placeAccessibility.entity.BuildingAccessibility
import domain.placeAccessibility.repository.BuildingAccessibilityRepository

class JpaBuildingAccessibililtyRepository(
    private val entityManagerHolder: GlobalEntityManagerHolder,
) : JpaEntityRepositoryBase<BuildingAccessibility, String>(BuildingAccessibility::class.java, entityManagerHolder),
    BuildingAccessibilityRepository {
    override fun findByBuildingIds(buildingIds: Collection<String>): List<BuildingAccessibility> {
        val em = entityManagerHolder.get()
        val query = em.createQuery("""
            SELECT ba
            FROM BuildingAccessibility ba
            WHERE ba.buildingId IN :buildingIds
        """.trimIndent(), BuildingAccessibility::class.java)
        query.setParameter("buildingIds", buildingIds)
        return query.resultList
    }
}
