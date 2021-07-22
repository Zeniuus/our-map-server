package domain.village.service

import domain.place.repository.BuildingRepository
import domain.place.repository.PlaceRepository
import domain.placeAccessibility.repository.BuildingAccessibilityRepository
import domain.placeAccessibility.repository.PlaceAccessibilityRepository
import domain.util.EntityIdRandomGenerator
import domain.village.entity.EupMyeonDong
import domain.village.entity.Village
import domain.village.repository.VillageRepository

class VillageService(
    private val buildingRepository: BuildingRepository,
    private val placeRepository: PlaceRepository,
    private val buildingAccessibilityRepository: BuildingAccessibilityRepository,
    private val placeAccessibilityRepository: PlaceAccessibilityRepository,
    private val villageRepository: VillageRepository,
) {
    fun listByRanking(): List<Village> {
        return villageRepository.listAll()
            .sortedByDescending { it.registerProgress }
    }

    fun upsertStatistics(eupMyeonDong: EupMyeonDong): Village {
        val buildingAccessibilities = buildingAccessibilityRepository.findByEupMyeonDong(eupMyeonDong)
        val buildingAccessibilitiesByUserId = buildingAccessibilities
            .filter { it.userId != null }
            .groupBy { it.userId!! }
        val existingVillage = villageRepository.findByEupMyeonDong(eupMyeonDong)
        return if (existingVillage != null) {
            existingVillage.buildingAccessibilityCount = buildingAccessibilities.size
            existingVillage.placeAccessibilityCount = placeAccessibilityRepository.countByEupMyeonDong(eupMyeonDong)
            existingVillage.buildingAccessibilityRegisteredUserCount = buildingAccessibilitiesByUserId.size
            existingVillage.mostBuildingAccessibilityRegisteredUserId = buildingAccessibilitiesByUserId.maxByOrNull { it.value.size }!!.key
            existingVillage
        } else {
            villageRepository.add(Village(
                id = EntityIdRandomGenerator.generate(),
                eupMyeonDongId = eupMyeonDong.id,
                buildingCount = buildingRepository.countByEupMyeonDong(eupMyeonDong),
                placeCount = placeRepository.countByEupMyeonDong(eupMyeonDong),
                buildingAccessibilityCount = buildingAccessibilities.size,
                placeAccessibilityCount = placeAccessibilityRepository.countByEupMyeonDong(eupMyeonDong),
                buildingAccessibilityRegisteredUserCount = buildingAccessibilitiesByUserId.size,
                mostBuildingAccessibilityRegisteredUserId = buildingAccessibilitiesByUserId.maxByOrNull { it.value.size }?.key,
            ))
        }
    }
}
