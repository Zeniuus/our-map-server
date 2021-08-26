package application.place

import application.TransactionManager
import domain.accessibility.entity.BuildingAccessibility
import domain.accessibility.entity.PlaceAccessibility
import domain.accessibility.service.SearchAccessibilityService
import domain.logging.OurMapEvent
import domain.logging.OurMapEventLogger
import domain.logging.SearchPlacesEvent
import domain.place.entity.Place
import domain.place.service.SearchPlaceService
import domain.util.Length
import domain.util.Location
import domain.village.repository.EupMyeonDongRepository
import domain.village.repository.SiGunGuRepository

class PlaceApplicationService(
    private val transactionManager: TransactionManager,
    private val siGunGuRepository: SiGunGuRepository,
    private val eupMyeonDongRepository: EupMyeonDongRepository,
    private val searchPlaceService: SearchPlaceService,
    private val searchAccessibilityService: SearchAccessibilityService,
    private val eventLogger: OurMapEventLogger,
) {
    data class SearchPlaceResult(
        val place: Place,
        val placeAccessibility: PlaceAccessibility?,
        val buildingAccessibility: BuildingAccessibility?,
    )

    fun searchPlaces(
        searchText: String,
        location: Location,
        maxDistance: Length? = null,
        siGunGuId: String? = null,
        eupMyeonDongId: String? = null,
    ): List<SearchPlaceResult> = transactionManager.doInTransaction {
        val places = searchPlaceService.searchPlaces(
            SearchPlaceService.SearchOption(
            searchText = searchText,
            location = location,
            maxDistance = maxDistance,
            siGunGu = siGunGuId?.let { siGunGuRepository.findById(it) },
            eupMyeonDong = eupMyeonDongId?.let { eupMyeonDongRepository.findById(it) },
        ))
        val accessibilitySearchResult = searchAccessibilityService.search(places)
        val result = places.map { place ->
            val (placeAccessibility, buildingAccessibility) = accessibilitySearchResult.getAccessibility(place)
            SearchPlaceResult(
                place = place,
                placeAccessibility = placeAccessibility,
                buildingAccessibility = buildingAccessibility,
            )
        }

        eventLogger.log(OurMapEvent(
            searchPlacesEvent = SearchPlacesEvent(
                keyword = searchText,
            )
        ))

        result
    }
}
