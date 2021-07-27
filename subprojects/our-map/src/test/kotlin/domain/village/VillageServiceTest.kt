package domain.village

import domain.DomainTestBase
import domain.place.repository.BuildingRepository
import domain.place.repository.PlaceRepository
import domain.placeAccessibility.entity.BuildingStairInfo
import domain.placeAccessibility.placeAccessibilityDomainModule
import domain.placeAccessibility.repository.BuildingAccessibilityRepository
import domain.placeAccessibility.repository.PlaceAccessibilityRepository
import domain.placeAccessibility.service.BuildingAccessibilityService
import domain.placeAccessibility.service.PlaceAccessibilityService
import domain.village.repository.EupMyeonDongRepository
import domain.village.repository.VillageRepository
import domain.village.service.VillageService
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.koin.test.inject

class VillageServiceTest : DomainTestBase() {
    override val koinModules = listOf(placeAccessibilityDomainModule, villageDomainModule)

    private val placeRepository by inject<PlaceRepository>()
    private val buildingRepository by inject<BuildingRepository>()
    private val placeAccessibilityRepository by inject<PlaceAccessibilityRepository>()
    private val buildingAccessibilityRepository by inject<BuildingAccessibilityRepository>()
    private val villageRepository by inject<VillageRepository>()
    private val eupMyeonDongRepository by inject<EupMyeonDongRepository>()
    private val villageService by inject<VillageService>()
    private val buildingAccessibilityService by inject<BuildingAccessibilityService>()
    private val placeAccessibilityService by inject<PlaceAccessibilityService>()

    @Before
    fun setUp() {
        transactionManager.doInTransaction {
            placeAccessibilityRepository.removeAll()
            buildingAccessibilityRepository.removeAll()
            placeRepository.removeAll()
            buildingRepository.removeAll()
            villageRepository.removeAll()
        }
    }

    @Test
    fun `upsertStatistics - 정상적인 경우`() = transactionManager.doAndRollback {
        val eupMyeonDong = eupMyeonDongRepository.listAll()[0]

        val placeCount = 100
        val places = (1..placeCount).map {
            testDataGenerator.createPlace(
                eupMyeonDongId = eupMyeonDong.id,
                siGunGuId = eupMyeonDong.siGunGu.id
            )
        }

        val village1 = villageService.upsertStatistics(eupMyeonDong)
        Assert.assertEquals(eupMyeonDong.id, village1.eupMyeonDongId)
        Assert.assertEquals(placeCount, village1.buildingCount)
        Assert.assertEquals(placeCount, village1.placeCount)
        Assert.assertEquals(0, village1.buildingAccessibilityCount)
        Assert.assertEquals(0, village1.placeAccessibilityCount)
        Assert.assertEquals(0, village1.buildingAccessibilityRegisteredUserCount)
        Assert.assertEquals(null, village1.mostBuildingAccessibilityRegisteredUserId)

        places.take(placeCount / 2).forEach { place ->
            buildingAccessibilityService.create(BuildingAccessibilityService.CreateParams(
                buildingId = place.building.id,
                hasElevator = true,
                hasObstacleToElevator = true,
                stairInfo = BuildingStairInfo.NONE,
                userId = null
            ))
            placeAccessibilityService.create(PlaceAccessibilityService.CreateParams(
                placeId = place.id,
                isFirstFloor = true,
                hasStair = true,
                isWheelchairAccessible = true,
                userId = null
            ))
        }

        /**
         * [domain.placeAccessibility.service.PlaceAccessibilityEventPublisher.accessibilityRegistered] 때문에
         * 명시적으로 [VillageService.upsertStatistics]를 호출하지 않아도 된다.
         */
        val village2 = villageRepository.findById(village1.id)
        Assert.assertEquals(eupMyeonDong.id, village2.eupMyeonDongId)
        Assert.assertEquals(placeCount, village2.buildingCount)
        Assert.assertEquals(placeCount, village2.placeCount)
        Assert.assertEquals(placeCount / 2, village2.buildingAccessibilityCount)
        Assert.assertEquals(placeCount / 2, village2.placeAccessibilityCount)
        Assert.assertEquals(0, village2.buildingAccessibilityRegisteredUserCount)
        Assert.assertEquals(null, village2.mostBuildingAccessibilityRegisteredUserId)

        val user = testDataGenerator.createUser()
        places.takeLast(placeCount / 2).forEach { place ->
            buildingAccessibilityService.create(BuildingAccessibilityService.CreateParams(
                buildingId = place.building.id,
                hasElevator = true,
                hasObstacleToElevator = true,
                stairInfo = BuildingStairInfo.NONE,
                userId = user.id
            ))
            placeAccessibilityService.create(PlaceAccessibilityService.CreateParams(
                placeId = place.id,
                isFirstFloor = true,
                hasStair = true,
                isWheelchairAccessible = true,
                userId = user.id
            ))
        }

        val village3 = villageRepository.findById(village1.id)
        Assert.assertEquals(eupMyeonDong.id, village3.eupMyeonDongId)
        Assert.assertEquals(placeCount, village3.buildingCount)
        Assert.assertEquals(placeCount, village3.placeCount)
        Assert.assertEquals(placeCount, village3.buildingAccessibilityCount)
        Assert.assertEquals(placeCount, village3.placeAccessibilityCount)
        Assert.assertEquals(1, village3.buildingAccessibilityRegisteredUserCount)
        Assert.assertEquals(user.id, village3.mostBuildingAccessibilityRegisteredUserId)
    }
}
