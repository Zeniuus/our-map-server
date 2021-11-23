package route

import org.junit.Assert
import org.junit.Test
import ourMap.protocol.ListConqueredPlacesParams
import ourMap.protocol.ListConqueredPlacesResult

class ListConqueredPlacesTest : OurMapServerRouteTestBase() {
    @Test
    fun testListConqueredPlaces() = runRouteTest {
        val user = transactionManager.doInTransaction {
            testDataGenerator.createUser()
        }
        val testClient = getTestClient(user)
        val registeredCount = 10
        val places = transactionManager.doInTransaction {
            (1..registeredCount).map {
                val place = testDataGenerator.createBuildingAndPlace()
                testDataGenerator.registerBuildingAndPlaceAccessibility(place, user)
                place
            }
        }

        val params = ListConqueredPlacesParams.newBuilder()
            .build()
        testClient.request("/listConqueredPlaces", params).apply {
            val result = getResult(ListConqueredPlacesResult::class)
            Assert.assertEquals(registeredCount, result.itemsCount)
            result.itemsList.forEach { item ->
                val place = places.find { it.id == item.place.id }!!
                Assert.assertEquals(place.building.id, item.building.id)
                Assert.assertTrue(item.hasPlaceAccessibility)
                Assert.assertTrue(item.hasBuildingAccessibility)
                Assert.assertFalse(item.hasDistanceMeters())
            }
        }
    }
}
