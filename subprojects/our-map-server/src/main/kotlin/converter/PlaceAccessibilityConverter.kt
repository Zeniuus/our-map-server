package converter

import domain.accessibility.entity.PlaceAccessibility
import domain.user.repository.UserRepository
import ourMap.protocol.Common
import ourMap.protocol.Model

class PlaceAccessibilityConverter(
    private val userRepository: UserRepository,
) {
    fun toProto(placeAccessibility: PlaceAccessibility) = Model.PlaceAccessibility.newBuilder()
        .setId(placeAccessibility.id)
        .setIsFirstFloor(placeAccessibility.isFirstFloor)
        .setStairInfo(StairInfoConverter.toProto(placeAccessibility.stairInfo))
        .setHasSlope(placeAccessibility.hasSlope)
        .also {
            placeAccessibility.userId?.let { userId ->
                val user = userRepository.findById(userId)
                it.registeredUserName = Common.StringValue.newBuilder().setValue(user.nickname).build()
            }
        }
        .setPlaceId(placeAccessibility.placeId)
        .build()!!
}
