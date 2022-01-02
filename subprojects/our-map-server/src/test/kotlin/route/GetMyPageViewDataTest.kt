package route

import domain.accessibility.repository.BuildingAccessibilityRepository
import domain.accessibility.repository.BuildingAccessibilityUpvoteRepository
import domain.accessibility.repository.PlaceAccessibilityRepository
import domain.place.repository.BuildingRepository
import domain.place.repository.PlaceRepository
import domain.user.entity.User
import domain.village.repository.EupMyeonDongRepository
import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.koin.test.inject
import ourMap.protocol.GetMyPageViewDataParams
import ourMap.protocol.GetMyPageViewDataResult

class GetMyPageViewDataTest : OurMapServerRouteTestBase() {
    private val placeRepository by inject<PlaceRepository>()
    private val buildingRepository by inject<BuildingRepository>()
    private val placeAccessibilityRepository by inject<PlaceAccessibilityRepository>()
    private val buildingAccessibilityRepository by inject<BuildingAccessibilityRepository>()
    private val buildingAccessibilityUpvoteRepository by inject<BuildingAccessibilityUpvoteRepository>()
    private val eupMyeonDongRepository by inject<EupMyeonDongRepository>()

    @Before
    fun setUp() = transactionManager.doInTransaction {
        buildingAccessibilityUpvoteRepository.removeAll()
        placeAccessibilityRepository.removeAll()
        buildingAccessibilityRepository.removeAll()
        placeRepository.removeAll()
        buildingRepository.removeAll()
    }

    @Test
    fun `favoriteVillages가 제대로 내려온다`() = runRouteTest {
        val (user, favoriteVillage) = transactionManager.doInTransaction {
            val user = testDataGenerator.createUser()
            val place = testDataGenerator.createBuildingAndPlace()
            val buildingAccessibility = testDataGenerator.registerBuildingAndPlaceAccessibility(place, user).second
            val favoriteVillage = testDataGenerator.getRandomVillage()
            testDataGenerator.registerFavoriteVillage(user, favoriteVillage)

            Pair(user, favoriteVillage)
        }

        getTestClient(user).request("/getMyPageViewData", GetMyPageViewDataParams.getDefaultInstance()).apply {
            val result = getResult(GetMyPageViewDataResult::class)
            Assert.assertEquals(user.id, result.user.id)
            Assert.assertEquals(1, result.favoriteVillagesList.size)
            Assert.assertEquals(favoriteVillage.id, result.favoriteVillagesList[0].id)
        }
    }

    @Test
    @Ignore("GetVillageStatisticsTest의 PlaceAccessibility가 제대로 cleanup 되지 않아서 등수가 하나씩 밀려서 나옴")
    fun `정복 통계 데이터가 제대로 내려온다`() = runRouteTest {
        val (eupMyeonDong1, eupMyeonDong2) = eupMyeonDongRepository.listAll()
        fun createUserWithRegisteredCount(registeredCount: Int): User {
            return transactionManager.doInTransaction {
                val user = testDataGenerator.createUser()
                repeat(registeredCount) {
                    val eupMyeonDong = if (it % 2 == 0) {
                        eupMyeonDong1
                    } else {
                        eupMyeonDong2
                    }
                    val place = testDataGenerator.createBuildingAndPlace(eupMyeonDongId = eupMyeonDong.id)
                    testDataGenerator.registerPlaceAccessibility(place, user)
                }
                user
            }
        }

        listOf(
            Pair(20, createUserWithRegisteredCount(20)),
            Pair(7, createUserWithRegisteredCount(7)),
            Pair(3, createUserWithRegisteredCount(3)),
            Pair(1, createUserWithRegisteredCount(1)),
            Pair(0, createUserWithRegisteredCount(0)),
        ).forEachIndexed { idx, (expectedRegisteredCount, user) ->
            val expectedRank = idx + 1
            val expectedConquerLevelInfo = getConquerLevelInfo(expectedRegisteredCount, 0)
            getTestClient(user).request("/getMyPageViewData", GetMyPageViewDataParams.getDefaultInstance()).apply {
                val result = getResult(GetMyPageViewDataResult::class)
                Assert.assertEquals(user.id, result.user.id)
                Assert.assertEquals(expectedConquerLevelInfo.level, result.conquerLevelInfo.level)
                Assert.assertEquals(expectedConquerLevelInfo.description, result.conquerLevelInfo.description)
                Assert.assertEquals(expectedRegisteredCount > 0, result.hasConquerRank())
                if (result.hasConquerRank()) {
                    Assert.assertEquals(expectedRank, result.conquerRank.value)
                }
                Assert.assertEquals(expectedRegisteredCount, result.placeAccessibilityCount)
                val expectedEupMyeonDong1RegisteredCount = expectedRegisteredCount - expectedRegisteredCount / 2 // 짝수면 절반, 홀수면 절반의 올림. e.g. 20 -> 10, 7 -> 4
                val expectedEupMyeonDong2RegisteredCount = expectedRegisteredCount / 2 // 짝수면 절반, 홀수면 절반의 내림. e.g. 20 -> 10, 7 -> 3
                Assert.assertEquals(expectedEupMyeonDong1RegisteredCount, result.placeAccessibilityCountDetailEntriesList.find { it.eupMyeonDongName == eupMyeonDong1.name }?.count ?: 0)
                Assert.assertEquals(expectedEupMyeonDong2RegisteredCount, result.placeAccessibilityCountDetailEntriesList.find { it.eupMyeonDongName == eupMyeonDong2.name }?.count ?: 0)
            }
        }
    }
}
