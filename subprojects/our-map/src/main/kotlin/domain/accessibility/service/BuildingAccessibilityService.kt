package domain.accessibility.service

import domain.DomainException
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
        val entranceStairInfo: BuildingStairInfo,
        val hasSlope: Boolean,
        val hasElevator: Boolean,
        val elevatorStairInfo: BuildingStairInfo,
        val userId: String?,
    )

    fun create(params: CreateParams): BuildingAccessibility {
        if (buildingAccessibilityRepository.findByBuildingId(params.buildingId) != null) {
            throw DomainException("이미 접근성 정보가 등록된 건물입니다.")
        }
        return buildingAccessibilityRepository.add(
            BuildingAccessibility(
                id = EntityIdGenerator.generateRandom(),
                buildingId = params.buildingId,
                entranceStairInfo = params.entranceStairInfo,
                hasSlope = params.hasSlope,
                hasElevator = params.hasElevator,
                elevatorStairInfo = params.elevatorStairInfo,
                userId = params.userId,
                createdAt = clock.instant(),
            )
        )
    }
}
