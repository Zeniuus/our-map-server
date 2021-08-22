package converter

import domain.accessibility.entity.BuildingAccessibility
import domain.accessibility.entity.BuildingStairInfo
import domain.accessibility.repository.BuildingAccessibilityUpvoteRepository
import domain.accessibility.service.BuildingAccessibilityUpvoteService
import domain.user.entity.User
import domain.user.repository.UserRepository
import ourMap.protocol.Common
import ourMap.protocol.Model

class BuildingAccessibilityConverter(
    private val userRepository: UserRepository,
    private val buildingAccessibilityUpvoteRepository: BuildingAccessibilityUpvoteRepository,
    private val buildingAccessibilityUpvoteService: BuildingAccessibilityUpvoteService,
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

    fun toProto(buildingAccessibility: BuildingAccessibility, user: User?) = Model.BuildingAccessibility.newBuilder()
        .setId(buildingAccessibility.id)
        .setHasElevator(buildingAccessibility.hasElevator)
        .setHasObstacleToElevator(buildingAccessibility.hasObstacleToElevator)
        .setStairInfo(toProto(buildingAccessibility.stairInfo))
        .also {
            buildingAccessibility.userId?.let { registeredUserId ->
                val registeredUser = userRepository.findById(registeredUserId)
                it.registeredUserName = Common.StringValue.newBuilder().setValue(registeredUser.nickname).build()
            }
        }
        .setBuildingId(buildingAccessibility.buildingId)
        .setIsUpvoted(user?.let { buildingAccessibilityUpvoteService.isUpvoted(it, buildingAccessibility) } ?: false)
        .setTotalUpvoteCount(buildingAccessibilityUpvoteRepository.getTotalUpvoteCount(buildingAccessibility))
        .build()!!
}
