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
        val distance: Length?,
    )

    fun searchPlaces(
        searchText: String,
        location: Location?,
        maxDistance: Length? = null,
        siGunGuId: String? = null,
        eupMyeonDongId: String? = null,
    ): List<SearchPlaceResult> = transactionManager.doInTransaction {
        val placesWithMetadata = searchPlaceService.searchPlaces(
            SearchPlaceService.SearchOption(
            searchText = searchText,
            location = location,
            maxDistance = maxDistance,
            siGunGu = siGunGuId?.let { siGunGuRepository.findById(it) },
            eupMyeonDong = eupMyeonDongId?.let { eupMyeonDongRepository.findById(it) },
        ))
        val accessibilitySearchResult = searchAccessibilityService.search(placesWithMetadata.map { it.place })
        val result = placesWithMetadata.map { placeWithMetadata ->
            val (placeAccessibility, buildingAccessibility) = accessibilitySearchResult.getAccessibility(placeWithMetadata.place)
            SearchPlaceResult(
                place = placeWithMetadata.place,
                placeAccessibility = placeAccessibility,
                buildingAccessibility = buildingAccessibility,
                distance = placeWithMetadata.distance
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
