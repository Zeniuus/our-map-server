package domain.accessibility.repository

import domain.EntityRepository
import domain.accessibility.entity.BuildingAccessibility
import domain.village.entity.EupMyeonDong

interface BuildingAccessibilityRepository : EntityRepository<BuildingAccessibility, String> {
    fun findByBuildingIds(buildingIds: Collection<String>): List<BuildingAccessibility>
    fun findByBuildingId(buildingId: String): BuildingAccessibility?
    fun findByEupMyeonDong(eupMyeonDong: EupMyeonDong): List<BuildingAccessibility>
}
