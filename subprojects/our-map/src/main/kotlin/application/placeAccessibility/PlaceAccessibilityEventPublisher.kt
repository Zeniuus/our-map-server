package application.placeAccessibility

import domain.place.entity.Place
import domain.village.repository.EupMyeonDongRepository
import domain.village.service.VillageService

class PlaceAccessibilityEventPublisher(
    private val eupMyeonDongRepository: EupMyeonDongRepository,
    private val villageService: VillageService,
) {
    fun accessibilityRegistered(place: Place) {
        villageService.upsertStatistics(eupMyeonDongRepository.findById(place.eupMyeonDongId))
    }
}
