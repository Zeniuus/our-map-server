package domain.accessibility.service

import domain.accessibility.entity.BuildingAccessibility
import domain.accessibility.entity.BuildingStairInfo
import domain.accessibility.repository.BuildingAccessibilityRepository
import domain.util.EntityIdGenerator
import java.time.Clock

class BuildingAccessibilityService(
    private val clock: Clock,
    private val buildingAccessibilityRepository: BuildingAccessibilityRepository,
) {
    data class CreateParams(
        val buildingId: String,
        val hasElevator: Boolean,
        val hasObstacleToElevator: Boolean,
        val stairInfo: BuildingStairInfo,
        val userId: String?,
    )

    fun create(params: CreateParams): BuildingAccessibility {
        return buildingAccessibilityRepository.add(
            BuildingAccessibility(
                id = EntityIdGenerator.generateRandom(),
                buildingId = params.buildingId,
                hasElevator = params.hasElevator,
                hasObstacleToElevator = params.hasObstacleToElevator,
                stairInfo = params.stairInfo,
                userId = params.userId,
                createdAt = clock.instant(),
            )
        )
    }
}
