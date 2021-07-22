package route.converter

import domain.placeAccessibility.entity.BuildingAccessibility
import domain.placeAccessibility.entity.BuildingStairInfo
import domain.user.repository.UserRepository
import ourMap.protocol.Common
import ourMap.protocol.Model

class BuildingAccessibilityConverter(
    private val userRepository: UserRepository,
) {
    companion object {
        fun toProto(stairInfo: BuildingStairInfo) = when (stairInfo) {
            BuildingStairInfo.NONE -> Model.BuildingAccessibility.StairInfo.NONE
            BuildingStairInfo.LESS_THAN_FIVE -> Model.BuildingAccessibility.StairInfo.LESS_THAN_FIVE
            BuildingStairInfo.OVER_TEN -> Model.BuildingAccessibility.StairInfo.OVER_TEN
        }

        fun fromProto(stairInfo: Model.BuildingAccessibility.StairInfo) = when (stairInfo) {
            Model.BuildingAccessibility.StairInfo.NONE -> BuildingStairInfo.NONE
            Model.BuildingAccessibility.StairInfo.LESS_THAN_FIVE -> BuildingStairInfo.LESS_THAN_FIVE
            Model.BuildingAccessibility.StairInfo.OVER_TEN -> BuildingStairInfo.OVER_TEN
            else -> throw IllegalArgumentException("Invalid stairInfo: $stairInfo")
        }
    }

    fun toProto(buildingAccessibility: BuildingAccessibility) = Model.BuildingAccessibility.newBuilder()
        .setId(buildingAccessibility.id)
        .setHasElevator(buildingAccessibility.hasElevator)
        .setHasObstacleToElevator(buildingAccessibility.hasObstacleToElevator)
        .setStairInfo(toProto(buildingAccessibility.stairInfo))
        .also {
            buildingAccessibility.userId?.let { userId ->
                val user = userRepository.findById(userId)
                it.registeredUserName = Common.StringValue.newBuilder().setValue(user.nickname).build()
            }
        }
        .setBuildingId(buildingAccessibility.buildingId)
        .build()!!
}
