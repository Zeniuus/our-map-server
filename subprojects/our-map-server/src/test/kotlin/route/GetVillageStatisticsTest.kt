package route

import application.village.VillageApplicationService
import domain.place.repository.BuildingRepository
import domain.place.repository.PlaceRepository
import domain.placeAccessibility.repository.BuildingAccessibilityRepository
import domain.placeAccessibility.repository.PlaceAccessibilityRepository
import domain.village.repository.EupMyeonDongRepository
import domain.village.repository.VillageRepository
import domain.village.service.VillageService
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.koin.test.inject
import ourMap.protocol.GetVillageStatisticsParams
import ourMap.protocol.GetVillageStatisticsResult

class GetVillageStatisticsTest : OurMapServerRouteTestBase() {
    private val placeRepository by inject<PlaceRepository>()
    private val buildingRepository by inject<BuildingRepository>()
    private val placeAccessibilityRepository by inject<PlaceAccessibilityRepository>()
    private val buildingAccessibilityRepository by inject<BuildingAccessibilityRepository>()
    private val villageRepository by inject<VillageRepository>()
    private val eupMyeonDongRepository by inject<EupMyeonDongRepository>()
    private val villageService by inject<VillageService>()
    private val villageApplicationService by inject<VillageApplicationService>()

    @Before
    fun setUp() = transactionManager.doInTransaction {
        placeAccessibilityRepository.removeAll()
        buildingAccessibilityRepository.removeAll()
        placeRepository.removeAll()
        buildingRepository.removeAll()
    }

    @Test
    fun getVillageStatisticsTest() = runRouteTest {
        val (village, eupMyeonDong, user) = transactionManager.doInTransaction {
            val randomVillage = villageRepository.listAll().shuffled()[0]
            val eupMyeonDong = eupMyeonDongRepository.findById(randomVillage.eupMyeonDongId)
            val user = testDataGenerator.createUser(instagramId = "instagramId")

            repeat(100) { idx ->
                val place = testDataGenerator.createPlace(
                    eupMyeonDongId = eupMyeonDong.id,
                    siGunGuId = eupMyeonDong.siGunGu.id
                )

                if (idx % 2 == 0) {
                    testDataGenerator.registerPlaceAccessibility(place, user)
                }
            }

            villageApplicationService.upsertAll()

            Triple(randomVillage, eupMyeonDong, user)
        }

        val params = GetVillageStatisticsParams.newBuilder()
            .setVillageId(village.id)
            .build()
        getTestClient(user).request("/getVillageStatistics", params).apply {
            val result = getResult(GetVillageStatisticsResult::class)
            Assert.assertEquals(village.id, result.village.id)
            Assert.assertEquals(villageService.getName(village), result.village.name)
            Assert.assertFalse(result.village.isFavoriteVillage)
            Assert.assertEquals(1, result.progressRank)
            Assert.assertEquals("50", result.progressPercentage)
            Assert.assertEquals(50, result.buildingAccessibilityCount)
            Assert.assertEquals(100, result.totalBuildingCount)
            Assert.assertEquals(50, result.placeAccessibilityCount)
            Assert.assertEquals(100, result.totalPlaceCount)
            Assert.assertEquals(1, result.registeredUserCount)
            Assert.assertEquals(eupMyeonDong.name, result.eupMyeonDongName)
            Assert.assertEquals(user.id, result.mostRegisteredUser.id)
            Assert.assertEquals(user.nickname, result.mostRegisteredUser.nickname)
            Assert.assertEquals(user.instagramId, result.mostRegisteredUser.instagramId.value)
            Assert.assertEquals(0, result.nextColoringRemainingCount) // TODO
        }
    }
}
