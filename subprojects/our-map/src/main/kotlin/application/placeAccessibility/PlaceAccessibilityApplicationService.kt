package application.placeAccessibility

import domain.placeAccessibility.entity.BuildingAccessibility
import domain.placeAccessibility.entity.PlaceAccessibility
import domain.placeAccessibility.service.BuildingAccessibilityService
import domain.placeAccessibility.service.PlaceAccessibilityService

class PlaceAccessibilityApplicationService(
    private val placeAccessibilityService: PlaceAccessibilityService,
    private val buildingAccessibilityService: BuildingAccessibilityService,
) {
    fun register(
        createPlaceAccessibilityParams: PlaceAccessibilityService.CreateParams,
        createBuildingAccessibilityParams: BuildingAccessibilityService.CreateParams?,
    ): Pair<PlaceAccessibility, BuildingAccessibility?> {
        val placeAccessibility = placeAccessibilityService.create(createPlaceAccessibilityParams)
        val buildingAccessibility = createBuildingAccessibilityParams?.let { buildingAccessibilityService.create(createBuildingAccessibilityParams) }
        return Pair(placeAccessibility, buildingAccessibility)
    }
}
