package application.place

import application.TransactionManager
import domain.place.entity.Place
import domain.place.service.SearchPlaceService
import domain.accessibility.entity.BuildingAccessibility
import domain.accessibility.entity.PlaceAccessibility
import domain.accessibility.service.SearchAccessibilityService
import domain.util.Location

class PlaceApplicationService(
    private val transactionManager: TransactionManager,
    private val searchPlaceService: SearchPlaceService,
    private val searchAccessibilityService: SearchAccessibilityService,
) {
    data class SearchPlaceResult(
        val place: Place,
        val placeAccessibility: PlaceAccessibility?,
        val buildingAccessibility: BuildingAccessibility?,
    )

    fun searchPlaces(searchText: String, location: Location): List<SearchPlaceResult> = transactionManager.doInTransaction {
        val places = searchPlaceService.searchPlaces(searchText, location)
        val accessibilitySearchResult = searchAccessibilityService.search(places)
        places.map { place ->
            val (placeAccessibility, buildingAccessibility) = accessibilitySearchResult.getAccessibility(place)
            SearchPlaceResult(
                place = place,
                placeAccessibility = placeAccessibility,
                buildingAccessibility = buildingAccessibility,
            )
        }
    }
}
