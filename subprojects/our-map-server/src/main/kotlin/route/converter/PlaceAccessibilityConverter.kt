package route.converter

import domain.placeAccessibility.entity.PlaceAccessibility
import ourMap.protocol.Model

object PlaceAccessibilityConverter {
    fun toProto(placeAccessibility: PlaceAccessibility) = Model.PlaceAccessibility.newBuilder()
        .setId(placeAccessibility.id)
        .setIsFirstFloor(placeAccessibility.isFirstFloor)
        .setHasStair(placeAccessibility.hasStair)
        .setIsWheelchairAccessible(placeAccessibility.isWheelchairAccessible)
        .setLikeCount(0) // TODO
        .build()
}
