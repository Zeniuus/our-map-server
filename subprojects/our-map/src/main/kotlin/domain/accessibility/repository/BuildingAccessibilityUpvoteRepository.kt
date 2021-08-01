package domain.accessibility.repository

import domain.EntityRepository
import domain.accessibility.entity.BuildingAccessibility
import domain.accessibility.entity.BuildingAccessibilityUpvote
import domain.user.entity.User

interface BuildingAccessibilityUpvoteRepository : EntityRepository<BuildingAccessibilityUpvote, String> {
    fun findByUserAndBuildingAccessibilityAndNotDeleted(user: User, buildingAccessibility: BuildingAccessibility): BuildingAccessibilityUpvote?
    fun getTotalUpvoteCount(user: User): Int
    fun getTotalUpvoteCount(buildingAccessibility: BuildingAccessibility): Int
}
