package application.accessibility

import application.TransactionIsolationLevel
import application.TransactionManager
import domain.accessibility.entity.BuildingAccessibility
import domain.accessibility.entity.PlaceAccessibility
import domain.accessibility.repository.BuildingAccessibilityRepository
import domain.accessibility.repository.PlaceAccessibilityRepository
import domain.accessibility.service.BuildingAccessibilityService
import domain.accessibility.service.PlaceAccessibilityService
import domain.place.repository.PlaceRepository

class AccessibilityApplicationService(
    private val transactionManager: TransactionManager,
    private val placeRepository: PlaceRepository,
    private val placeAccessibilityRepository: PlaceAccessibilityRepository,
    private val buildingAccessibilityRepository: BuildingAccessibilityRepository,
    private val placeAccessibilityService: PlaceAccessibilityService,
    private val buildingAccessibilityService: BuildingAccessibilityService,
) {
    fun getAccessibility(placeId: String): Pair<PlaceAccessibility?, BuildingAccessibility?> = transactionManager.doInTransaction {
        val placeAccessibility = placeAccessibilityRepository.findByPlaceId(placeId)
        val place = placeRepository.findById(placeId)
        val buildingAccessibility = buildingAccessibilityRepository.findByBuildingId(place.building.id)

        Pair(placeAccessibility, buildingAccessibility)
    }

    fun register(
        createPlaceAccessibilityParams: PlaceAccessibilityService.CreateParams,
        createBuildingAccessibilityParams: BuildingAccessibilityService.CreateParams?,
    ): Pair<PlaceAccessibility, BuildingAccessibility?> = transactionManager.doInTransaction(TransactionIsolationLevel.SERIALIZABLE) {
        // TODO: 건물 정보 / 장소 정보 이미 있으면 에러 내기
        val placeAccessibility = placeAccessibilityService.create(createPlaceAccessibilityParams)
        val buildingAccessibility = createBuildingAccessibilityParams?.let { buildingAccessibilityService.create(createBuildingAccessibilityParams) }
        Pair(placeAccessibility, buildingAccessibility)
    }
}
