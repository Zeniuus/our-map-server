package domain.place.service

import domain.place.entity.Place
import domain.place.repository.PlaceRepository
import domain.util.Location
import domain.util.LocationUtils

class SearchPlaceService(
    private val placeRepository: PlaceRepository
) {
    fun searchPlaces(searchText: String, location: Location): List<Place> {
        val normalizedSearchText = searchText.trim()
        // 검색어가 너무 짧으면 검색을 하지 않는다.
        if (normalizedSearchText.length <= 1) {
            return emptyList()
        }
        return placeRepository.findByNameContains(searchText)
            .sortedBy { LocationUtils.calculateDistance(location, it.location) }
    }
}
