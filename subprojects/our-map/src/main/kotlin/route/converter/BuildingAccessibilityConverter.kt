package route.converter

import domain.placeAccessibility.entity.BuildingAccessibility
import domain.placeAccessibility.entity.BuildingStairInfo
import ourMap.protocol.Model

object BuildingAccessibilityConverter {
    fun toProto(buildingAccessibility: BuildingAccessibility) = Model.BuildingAccessibility.newBuilder()
        .setId(buildingAccessibility.id)
        .setHasElevator(buildingAccessibility.hasElevator)
        .setHasObstacleToElevator(buildingAccessibility.hasObsticleToElevator)
        .setStairInfo(toProto(buildingAccessibility.stairInfo))
        .build()

    fun toProto(stairInfo: BuildingStairInfo) = when (stairInfo) {
        BuildingStairInfo.NONE -> Model.BuildingAccessibility.StairInfo.NONE
        BuildingStairInfo.LESS_THAN_FIVE -> Model.BuildingAccessibility.StairInfo.LESS_THAN_FIVE
        BuildingStairInfo.OVER_TEN -> Model.BuildingAccessibility.StairInfo.OVER_TEN
    }
}
