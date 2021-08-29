package application.accessibility

import domain.accessibility.entity.BuildingAccessibility
import domain.accessibility.entity.PlaceAccessibility
import domain.place.repository.PlaceRepository
import domain.village.repository.EupMyeonDongRepository
import domain.village.service.VillageService

class AccessibilityRegisteredEventHandler(
    private val placeRepository: PlaceRepository,
    private val eupMyeonDongRepository: EupMyeonDongRepository,
    private val villageService: VillageService,
) {
    fun handle(placeAccessibility: PlaceAccessibility, buildingAccessibility: BuildingAccessibility?) {
        val place = placeRepository.findById(placeAccessibility.placeId)
        val eupMyeonDong = eupMyeonDongRepository.findById(place.eupMyeonDongId)
        villageService.upsertStatistics(eupMyeonDong)
    }
}
