package domain.accessibility.repository

import domain.EntityRepository
import domain.accessibility.entity.BuildingAccessibilityComment

interface BuildingAccessibilityCommentRepository : EntityRepository<BuildingAccessibilityComment, String> {
    fun findByBuildingId(buildingId: String): List<BuildingAccessibilityComment>
}
