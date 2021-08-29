package domain.accessibility.service

import domain.DomainException
import domain.accessibility.entity.PlaceAccessibility
import domain.accessibility.repository.PlaceAccessibilityRepository
import domain.util.EntityIdGenerator
import java.time.Clock

class PlaceAccessibilityService(
    private val clock: Clock,
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
        if (placeAccessibilityRepository.findByPlaceId(params.placeId) != null) {
            throw DomainException("이미 접근성 정보가 등록된 장소입니다.")
        }
        val result = placeAccessibilityRepository.add(
            PlaceAccessibility(
                id = EntityIdGenerator.generateRandom(),
                placeId = params.placeId,
                isFirstFloor = params.isFirstFloor,
                hasStair = params.hasStair,
                isWheelchairAccessible = params.isWheelchairAccessible,
                userId = params.userId,
                createdAt = clock.instant(),
            )
        )

        placeAccessibilityEventPublisher.accessibilityRegistered(result)

        return result
    }
}
