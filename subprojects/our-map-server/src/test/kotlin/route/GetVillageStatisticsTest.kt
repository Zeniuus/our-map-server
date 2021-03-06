package route

import application.village.VillageApplicationService
import domain.accessibility.repository.BuildingAccessibilityRepository
import domain.accessibility.repository.BuildingAccessibilityUpvoteRepository
import domain.accessibility.repository.PlaceAccessibilityRepository
import domain.place.repository.BuildingRepository
import domain.place.repository.PlaceRepository
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
    private val buildingAccessibilityUpvoteRepository by inject<BuildingAccessibilityUpvoteRepository>()
    private val villageRepository by inject<VillageRepository>()
    private val eupMyeonDongRepository by inject<EupMyeonDongRepository>()
    private val villageService by inject<VillageService>()
    private val villageApplicationService by inject<VillageApplicationService>()

    @Before
    fun setUp() = transactionManager.doInTransaction {
        placeAccessibilityRepository.removeAll()
        buildingAccessibilityUpvoteRepository.removeAll()
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
                val place = testDataGenerator.createBuildingAndPlace(
                    eupMyeonDongId = eupMyeonDong.id,
                    siGunGuId = eupMyeonDong.siGunGu.id
                )

                if (idx % 2 == 0) {
                    testDataGenerator.registerBuildingAndPlaceAccessibility(place, user)
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
            Assert.assertEquals(village.id, result.villageRankingEntry.village.id)
            Assert.assertEquals(villageService.getName(village), result.villageRankingEntry.village.name)
            Assert.assertFalse(result.villageRankingEntry.village.isFavoriteVillage)
            Assert.assertEquals(1, result.villageRankingEntry.progressRank)
            Assert.assertEquals("50", result.villageRankingEntry.progressPercentage)
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
