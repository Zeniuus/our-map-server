package route

import domain.accessibility.repository.BuildingAccessibilityRepository
import domain.accessibility.repository.PlaceAccessibilityRepository
import domain.place.repository.BuildingRepository
import domain.place.repository.PlaceRepository
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.koin.test.inject
import ourMap.protocol.GetMyPageViewDataParams
import ourMap.protocol.GetMyPageViewDataResult

class GetMyPageViewDataTest : OurMapServerRouteTestBase() {
    private val placeRepository by inject<PlaceRepository>()
    private val buildingRepository by inject<BuildingRepository>()
    private val placeAccessibilityRepository by inject<PlaceAccessibilityRepository>()
    private val buildingAccessibilityRepository by inject<BuildingAccessibilityRepository>()

    @Before
    fun setUp() = transactionManager.doInTransaction {
        placeAccessibilityRepository.removeAll()
        buildingAccessibilityRepository.removeAll()
        placeRepository.removeAll()
        buildingRepository.removeAll()
    }

    @Test
    fun getMyPageViewDataTest() = runRouteTest {
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
}
