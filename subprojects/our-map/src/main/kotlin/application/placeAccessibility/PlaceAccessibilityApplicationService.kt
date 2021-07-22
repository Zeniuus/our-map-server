package application.placeAccessibility

import application.TransactionManager
import domain.place.repository.PlaceRepository
import domain.placeAccessibility.entity.BuildingAccessibility
import domain.placeAccessibility.entity.PlaceAccessibility
import domain.placeAccessibility.repository.BuildingAccessibilityRepository
import domain.placeAccessibility.repository.PlaceAccessibilityRepository
import domain.placeAccessibility.service.BuildingAccessibilityService
import domain.placeAccessibility.service.PlaceAccessibilityService

class PlaceAccessibilityApplicationService(
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
    ): Pair<PlaceAccessibility, BuildingAccessibility?> = transactionManager.doInTransaction {
        val placeAccessibility = placeAccessibilityService.create(createPlaceAccessibilityParams)
        val buildingAccessibility = createBuildingAccessibilityParams?.let { buildingAccessibilityService.create(createBuildingAccessibilityParams) }
        Pair(placeAccessibility, buildingAccessibility)
    }
}
