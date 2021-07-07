package domain.placeAccessibility.service

import domain.placeAccessibility.entity.BuildingAccessibility
import domain.placeAccessibility.entity.BuildingStairInfo
import domain.placeAccessibility.repository.BuildingAccessibilityRepository
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
                hasObsticleToElevator = params.hasObstacleToElevator,
                stairInfo = params.stairInfo,
                userId = params.userId,
            )
        )
    }
}
