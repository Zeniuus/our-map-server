package domain.village.service

import domain.accessibility.repository.BuildingAccessibilityRepository
import domain.accessibility.repository.PlaceAccessibilityRepository
import domain.place.repository.BuildingRepository
import domain.place.repository.PlaceRepository
import domain.util.EntityIdRandomGenerator
import domain.village.entity.EupMyeonDong
import domain.village.entity.Village
import domain.village.repository.EupMyeonDongRepository
import domain.village.repository.VillageRepository

class VillageService(
    private val buildingRepository: BuildingRepository,
    private val placeRepository: PlaceRepository,
    private val buildingAccessibilityRepository: BuildingAccessibilityRepository,
    private val placeAccessibilityRepository: PlaceAccessibilityRepository,
    private val villageRepository: VillageRepository,
    private val eupMyeonDongRepository: EupMyeonDongRepository,
) {
    fun getName(village: Village): String {
        val eupMyeonDong = eupMyeonDongRepository.findById(village.eupMyeonDongId)
        return "${eupMyeonDong.siGunGu.name} ${eupMyeonDong.name}"
    }

    fun getProgressRank(village: Village): Int {
        val villages = villageRepository.listAll()
            .sortedByDescending { it.registerProgress }
        return villages.indexOfFirst { it.id == village.id } + 1
    }

    fun upsertStatistics(eupMyeonDong: EupMyeonDong): Village {
        val buildingAccessibilities = buildingAccessibilityRepository.findByEupMyeonDong(eupMyeonDong)
        val buildingAccessibilitiesByUserId = buildingAccessibilities
            .filter { it.userId != null }
            .groupBy { it.userId!! }
        val existingVillage = villageRepository.findByEupMyeonDong(eupMyeonDong)
        return if (existingVillage != null) {
            // 원래 buildingCount와 placeCount는 다시 계산할 필요가 없으나,
            // 테스트할 때는 building과 place 개수가 중간에 변하는 경우가 있기도 하고
            // 나중에 building과 place를 서비스 도중 등록하는 경우도 고려해서 그냥 갱신해준다.
            existingVillage.buildingCount = buildingRepository.countByEupMyeonDong(eupMyeonDong)
            existingVillage.placeCount = placeRepository.countByEupMyeonDong(eupMyeonDong)
            existingVillage.buildingAccessibilityCount = buildingAccessibilities.size
            existingVillage.placeAccessibilityCount = placeAccessibilityRepository.countByEupMyeonDong(eupMyeonDong)
            existingVillage.buildingAccessibilityRegisteredUserCount = buildingAccessibilitiesByUserId.size
            existingVillage.mostBuildingAccessibilityRegisteredUserId = buildingAccessibilitiesByUserId.maxByOrNull { it.value.size }?.key
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
