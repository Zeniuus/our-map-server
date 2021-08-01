package domain.accessibility.service

import domain.accessibility.entity.BuildingAccessibility
import domain.accessibility.entity.BuildingStairInfo
import domain.accessibility.repository.BuildingAccessibilityRepository
import domain.util.EntityIdRandomGenerator

class BuildingAccessibilityService(
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
                id = EntityIdRandomGenerator.generate(),
                buildingId = params.buildingId,
                hasElevator = params.hasElevator,
                hasObstacleToElevator = params.hasObstacleToElevator,
                stairInfo = params.stairInfo,
                userId = params.userId,
            )
        )
    }
}
