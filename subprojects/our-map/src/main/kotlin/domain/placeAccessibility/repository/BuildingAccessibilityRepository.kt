package domain.placeAccessibility.repository

import domain.EntityRepository
import domain.placeAccessibility.entity.BuildingAccessibility

interface BuildingAccessibilityRepository : EntityRepository<BuildingAccessibility, String> {
    fun findByBuildingIds(buildingIds: Collection<String>): List<BuildingAccessibility>
    fun findByBuildingId(buildingId: String): BuildingAccessibility?
}
