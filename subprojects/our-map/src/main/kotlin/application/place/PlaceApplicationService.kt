package application.place

import domain.place.entity.Place
import domain.place.service.SearchPlaceService
import domain.placeAccessibility.entity.BuildingAccessibility
import domain.placeAccessibility.entity.PlaceAccessibility
import domain.placeAccessibility.service.SearchPlaceAccessibilityService
import domain.util.Location
import infra.persistence.repository.transactional

class PlaceApplicationService(
    private val searchPlaceService: SearchPlaceService,
    private val searchPlaceAccessibilityService: SearchPlaceAccessibilityService,
) {
    data class SearchPlaceResult(
        val place: Place,
        val placeAccessibility: PlaceAccessibility?,
        val buildingAccessibility: BuildingAccessibility?,
    )

    fun searchPlaces(searchText: String, location: Location): List<SearchPlaceResult> = transactional {
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
}
