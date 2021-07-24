package route

import application.village.VillageApplicationService
import domain.place.repository.BuildingRepository
import domain.place.repository.PlaceRepository
import domain.placeAccessibility.repository.BuildingAccessibilityRepository
import domain.placeAccessibility.repository.PlaceAccessibilityRepository
import domain.village.repository.EupMyeonDongRepository
import domain.village.repository.VillageRepository
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.koin.test.inject
import ourMap.protocol.GetMainViewDataParams
import ourMap.protocol.GetMainViewDataResult

class GetMainViewDataTest : OurMapServerRouteTestBase() {
    private val placeRepository by inject<PlaceRepository>()
    private val buildingRepository by inject<BuildingRepository>()
    private val placeAccessibilityRepository by inject<PlaceAccessibilityRepository>()
    private val buildingAccessibilityRepository by inject<BuildingAccessibilityRepository>()
    private val villageRepository by inject<VillageRepository>()
    private val eupMyeonDongRepository by inject<EupMyeonDongRepository>()
    private val villageApplicationService by inject<VillageApplicationService>()

    @Before
    fun setUp() = transactionManager.doInTransaction {
        transactionManager.doInTransaction {
            placeAccessibilityRepository.removeAll()
            buildingAccessibilityRepository.removeAll()
            placeRepository.removeAll()
            buildingRepository.removeAll()
        }
    }

    @Test
    fun testGetMainViewData() = runRouteTest {
        val user = transactionManager.doInTransaction {
            testDataGenerator.createUser()
        }

        val eupMyeonDongs = transactionManager.doInTransaction {
            placeAccessibilityRepository.removeAll()
            buildingAccessibilityRepository.removeAll()
            placeRepository.removeAll()
            buildingRepository.removeAll()

            val eupMyeonDongs = eupMyeonDongRepository.listAll().shuffled()
            eupMyeonDongs.forEachIndexed { idx, eupMyeonDong ->
                val places = (1..100).map {
                    testDataGenerator.createPlace(
                        eupMyeonDongId = eupMyeonDong.id,
                        siGunGuId = eupMyeonDong.siGunGu.id
                    )
                }
                repeat(idx) {
                    testDataGenerator.registerPlaceAccessibility(places[it], null)
                }
            }
            villageApplicationService.insertAll()

            eupMyeonDongs
        }

        val testClient = getTestClient(user)
        testClient.request("/getMainViewData", GetMainViewDataParams.getDefaultInstance()).apply {
            val result = getResult(GetMainViewDataResult::class)
            Assert.assertEquals(eupMyeonDongs.size, result.villageRankingEntriesList.size)
            transactionManager.doInTransaction {
                eupMyeonDongs.zip(result.villageRankingEntriesList.reversed()).forEachIndexed { idx, (eupMyeonDong, villageRankingEntry) ->
                    val village = villageRepository.findByEupMyeonDong(eupMyeonDong)!!
                    Assert.assertEquals(village.id, villageRankingEntry.villageId)
                    Assert.assertEquals("${eupMyeonDong.siGunGu.name} ${eupMyeonDong.name}", villageRankingEntry.villageName)
                    Assert.assertEquals("$idx", villageRankingEntry.progressPercentage)
                }
            }
        }
    }
}
