package route

import org.junit.Assert
import org.junit.Test
import ourMap.protocol.ListPlacesInBuildingParams
import ourMap.protocol.ListPlacesInBuildingResult
import kotlin.random.Random

class ListPlacesInBuildingTest : OurMapServerRouteTestBase() {
    @Test
    fun testListPlacesInBuilding() = runRouteTest {
        val user = transactionManager.doInTransaction {
            testDataGenerator.createUser()
        }
        val testClient = getTestClient(user)
        val placesCount = 10
        val accessibilityRegisteredPlaceIds = mutableSetOf<String>()
        val (building, places) = transactionManager.doInTransaction {
            val building = testDataGenerator.createBuilding()
            testDataGenerator.registerBuildingAccessibility(building)
            val places = (1..placesCount).map {
                val place = testDataGenerator.createPlace(placeName = Random.nextBytes(32).toString(), building = building)
                if (it % 3 == 0) {
                    testDataGenerator.registerPlaceAccessibility(place)
                    accessibilityRegisteredPlaceIds.add(place.id)
                }
                place
            }
            Pair(building, places)
        }

        val params1 = ListPlacesInBuildingParams.newBuilder()
            .setBuildingId(building.id)
            .build()
        testClient.request("/listPlacesInBuilding", params1).apply {
            val result = getResult(ListPlacesInBuildingResult::class)
            Assert.assertEquals(placesCount, result.itemsCount)
            result.itemsList.forEach { item ->
                val place = places.find { it.id == item.place.id }!!
                Assert.assertEquals(building.id, item.building.id)
                Assert.assertEquals(place.id in accessibilityRegisteredPlaceIds, item.hasPlaceAccessibility)
                Assert.assertTrue(item.hasBuildingAccessibility)
                Assert.assertFalse(item.hasDistanceMeters())
            }
        }
    }

    // TODO: 유저 없는 경우도 테스트?
}
