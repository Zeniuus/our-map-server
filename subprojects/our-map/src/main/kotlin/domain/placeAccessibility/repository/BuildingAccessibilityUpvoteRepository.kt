package domain.placeAccessibility.repository

import domain.EntityRepository
import domain.placeAccessibility.entity.BuildingAccessibility
import domain.placeAccessibility.entity.BuildingAccessibilityUpvote
import domain.user.entity.User

interface BuildingAccessibilityUpvoteRepository : EntityRepository<BuildingAccessibilityUpvote, String> {
    fun findByUserAndBuildingAccessibilityAndNotDeleted(user: User, buildingAccessibility: BuildingAccessibility): BuildingAccessibilityUpvote?
    fun getTotalUpvoteCount(user: User): Int
}
