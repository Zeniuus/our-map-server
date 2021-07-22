package domain.placeAccessibility.service

import domain.place.repository.PlaceRepository
import domain.placeAccessibility.entity.PlaceAccessibility
import domain.village.repository.EupMyeonDongRepository
import domain.village.service.VillageService

class PlaceAccessibilityEventPublisher(
    private val placeRepository: PlaceRepository,
    private val eupMyeonDongRepository: EupMyeonDongRepository,
    private val villageService: VillageService,
) {
    fun accessibilityRegistered(placeAccessibility: PlaceAccessibility) {
        val place = placeRepository.findById(placeAccessibility.placeId)
        val eupMyeonDong = eupMyeonDongRepository.findById(place.eupMyeonDongId)
        villageService.upsertStatistics(eupMyeonDong)
    }
}
