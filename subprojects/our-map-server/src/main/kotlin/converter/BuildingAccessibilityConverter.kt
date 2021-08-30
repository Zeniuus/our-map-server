package converter

import domain.accessibility.entity.BuildingAccessibility
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
    fun toProto(buildingAccessibility: BuildingAccessibility, user: User?) = Model.BuildingAccessibility.newBuilder()
        .setId(buildingAccessibility.id)
        .setEntranceStairInfo(StairInfoConverter.toProto(buildingAccessibility.entranceStairInfo))
        .setHasSlope(buildingAccessibility.hasSlope)
        .setHasElevator(buildingAccessibility.hasElevator)
        .setElevatorStairInfo(StairInfoConverter.toProto(buildingAccessibility.elevatorStairInfo))
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
