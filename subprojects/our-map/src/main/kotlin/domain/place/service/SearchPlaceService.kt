package domain.place.service

import domain.place.entity.Place
import domain.place.repository.PlaceRepository
import domain.util.Length
import domain.util.Location
import domain.util.LocationUtils
import domain.village.entity.EupMyeonDong
import domain.village.entity.SiGunGu

class SearchPlaceService(
    private val placeRepository: PlaceRepository
) {
    data class SearchOption(
        val searchText: String,
        val location: Location,
        val maxDistance: Length? = null,
        val siGunGu: SiGunGu? = null,
        val eupMyeonDong: EupMyeonDong? = null,
    )

    fun searchPlaces(searchOption: SearchOption): List<Place> {
        val normalizedSearchText = searchOption.searchText.trim()
        // 검색어가 너무 짧으면 검색을 하지 않는다.
        if (normalizedSearchText.length <= 1) {
            return emptyList()
        }
        return placeRepository.findByNameContains(normalizedSearchText)
            .asSequence()
            .filter { searchOption.siGunGu == null || it.siGunGuId == searchOption.siGunGu.id }
            .filter { searchOption.eupMyeonDong == null || it.eupMyeonDongId == searchOption.eupMyeonDong.id }
            .map { Pair(it, LocationUtils.calculateDistance(searchOption.location, it.location)) }
            .filter { searchOption.maxDistance == null || it.second <= searchOption.maxDistance }
            .sortedBy { it.second }
            .map { it.first }
            .toList()
    }
}
