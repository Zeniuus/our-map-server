package domain.accessibility.service

import domain.accessibility.entity.BuildingAccessibility
import domain.accessibility.entity.BuildingAccessibilityUpvote
import domain.accessibility.repository.BuildingAccessibilityUpvoteRepository
import domain.user.entity.User
import domain.util.EntityIdRandomGenerator
import java.time.Clock

class BuildingAccessibilityUpvoteService(
    private val clock: Clock,
    private val buildingAccessibilityUpvoteRepository: BuildingAccessibilityUpvoteRepository,
) {
    fun giveUpvote(user: User, buildingAccessibility: BuildingAccessibility): BuildingAccessibilityUpvote {
        val existingUpvote = buildingAccessibilityUpvoteRepository.findByUserAndBuildingAccessibilityAndNotDeleted(user, buildingAccessibility)
        if (existingUpvote != null) {
            return existingUpvote
        }

        return buildingAccessibilityUpvoteRepository.add(BuildingAccessibilityUpvote(
            id = EntityIdRandomGenerator.generate(),
            userId = user.id,
            buildingAccessibility = buildingAccessibility,
            createdAt = clock.instant()
        ))
    }

    fun cancelUpvote(user: User, buildingAccessibility: BuildingAccessibility) {
        buildingAccessibilityUpvoteRepository.findByUserAndBuildingAccessibilityAndNotDeleted(user, buildingAccessibility)?.let {
            it.deletedAt = clock.instant()
            buildingAccessibilityUpvoteRepository.add(it)
        }
    }

    fun isUpvoted(user: User, buildingAccessibility: BuildingAccessibility): Boolean {
        return buildingAccessibilityUpvoteRepository.findByUserAndBuildingAccessibilityAndNotDeleted(user, buildingAccessibility) != null
    }
}
