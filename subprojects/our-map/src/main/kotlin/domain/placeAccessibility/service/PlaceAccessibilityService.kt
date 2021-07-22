package domain.placeAccessibility.service

import domain.placeAccessibility.entity.PlaceAccessibility
import domain.placeAccessibility.repository.PlaceAccessibilityRepository
import domain.util.EntityIdRandomGenerator

class PlaceAccessibilityService(
    private val placeAccessibilityRepository: PlaceAccessibilityRepository,
    private val placeAccessibilityEventPublisher: PlaceAccessibilityEventPublisher,
) {
    data class CreateParams(
        val placeId: String,
        val isFirstFloor: Boolean,
        val hasStair: Boolean,
        val isWheelchairAccessible: Boolean,
        val userId: String?,
    )

    fun create(params: CreateParams): PlaceAccessibility {
        val result = placeAccessibilityRepository.add(
            PlaceAccessibility(
                id = EntityIdRandomGenerator.generate(),
                placeId = params.placeId,
                isFirstFloor = params.isFirstFloor,
                hasStair = params.hasStair,
                isWheelchairAccessible = params.isWheelchairAccessible,
                userId = params.userId,
            )
        )

        placeAccessibilityEventPublisher.accessibilityRegistered(result)

        return result
    }
}
