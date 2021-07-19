package application.place

import application.TransactionManager
import domain.place.entity.Place
import domain.place.repository.PlaceRepository
import domain.place.service.SearchPlaceService
import domain.placeAccessibility.entity.BuildingAccessibility
import domain.placeAccessibility.entity.PlaceAccessibility
import domain.placeAccessibility.repository.BuildingAccessibilityRepository
import domain.placeAccessibility.repository.PlaceAccessibilityRepository
import domain.placeAccessibility.service.SearchPlaceAccessibilityService
import domain.util.Location

class PlaceApplicationService(
    private val transactionManager: TransactionManager,
    private val placeRepository: PlaceRepository,
    private val placeAccessibilityRepository: PlaceAccessibilityRepository,
    private val buildingAccessibilityRepository: BuildingAccessibilityRepository,
    private val searchPlaceService: SearchPlaceService,
    private val searchPlaceAccessibilityService: SearchPlaceAccessibilityService,
) {
    data class SearchPlaceResult(
        val place: Place,
        val placeAccessibility: PlaceAccessibility?,
        val buildingAccessibility: BuildingAccessibility?,
    )

    fun searchPlaces(searchText: String, location: Location): List<SearchPlaceResult> = transactionManager.doInTransaction {
        val places = searchPlaceService.searchPlaces(searchText, location)
        val accessibilitySearchResult = searchPlaceAccessibilityService.search(places)
        places.map { place ->
            val (placeAccessibility, buildingAccessibility) = accessibilitySearchResult.getAccessibility(place)
            SearchPlaceResult(
                place = place,
                placeAccessibility = placeAccessibility,
                buildingAccessibility = buildingAccessibility,
            )
        }
    }

    fun getAccessibility(placeId: String): Pair<PlaceAccessibility?, BuildingAccessibility?> = transactionManager.doInTransaction {
        val placeAccessibility = placeAccessibilityRepository.findByPlaceId(placeId)
        val place = placeRepository.findById(placeId)!!
        val buildingAccessibility = buildingAccessibilityRepository.findByBuildingId(place.building.id)

        Pair(placeAccessibility, buildingAccessibility)
    }
}
