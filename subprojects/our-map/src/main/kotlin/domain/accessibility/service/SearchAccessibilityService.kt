package domain.accessibility.service

import domain.place.entity.Place
import domain.accessibility.entity.BuildingAccessibility
import domain.accessibility.entity.PlaceAccessibility
import domain.accessibility.repository.BuildingAccessibilityRepository
import domain.accessibility.repository.PlaceAccessibilityRepository

class SearchAccessibilityService(
    private val placeAccessibilityRepository: PlaceAccessibilityRepository,
    private val buildingAccessibilityRepository: BuildingAccessibilityRepository,
) {
    data class Result(
        private val places: List<Place>,
        private val placeAccessibilities: List<PlaceAccessibility>,
        private val buildingAccessibilities: List<BuildingAccessibility>,
    ) {
        private val accessibilityByPlaceId = run {
            val placeAccessibilityByPlaceId = placeAccessibilities.associateBy { it.placeId }
            val buildingAccessibilityByBuildingId = buildingAccessibilities.associateBy { it.buildingId }
            places.map {
                it.id to Pair(placeAccessibilityByPlaceId[it.id], buildingAccessibilityByBuildingId[it.building.id])
            }.toMap()
        }

        fun getAccessibility(place: Place): Pair<PlaceAccessibility?, BuildingAccessibility?> {
            return accessibilityByPlaceId[place.id] ?: throw IllegalArgumentException("search() 메소드의 인자로 넣은 place가 아닙니다.")
        }
    }

    fun search(places: List<Place>): Result {
        val buildingIds = places.map { it.building.id }
        val placeAccessibilities = placeAccessibilityRepository.findByPlaceIds(places.map { it.id })
        val buildingAccessibilities = buildingAccessibilityRepository.findByBuildingIds(buildingIds)
        return Result(
            places = places,
            placeAccessibilities = placeAccessibilities,
            buildingAccessibilities = buildingAccessibilities,
        )
    }
}
