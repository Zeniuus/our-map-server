package route

import org.junit.Assert
import org.junit.Test
import ourMap.protocol.Model
import ourMap.protocol.SearchPlacesParams
import ourMap.protocol.SearchPlacesResult
import kotlin.random.Random

class SearchPlacesTest : OurMapServerRouteTestBase() {
    @Test
    fun testSearchPlaces() = runRouteTest {
        val user = transactionManager.doInTransaction {
            testDataGenerator.createUser()
        }
        val testClient = getTestClient(user)
        val place = transactionManager.doInTransaction {
            testDataGenerator.createBuildingAndPlace(placeName = Random.nextBytes(32).toString())
        }

        val params1 = SearchPlacesParams.newBuilder()
            .setSearchText(place.name.substring(2, 5))
            .setCurrentLocation(Model.Location.newBuilder().setLng(place.lng).setLat(place.lat))
            .build()
        testClient.request("/searchPlaces", params1).apply {
            val result = getResult(SearchPlacesResult::class)
            Assert.assertEquals(1, result.itemsList.size)
            Assert.assertEquals(place.id, result.itemsList[0].place.id)
            Assert.assertTrue(result.itemsList[0].hasDistanceMeters())
        }

        val param2 = SearchPlacesParams.newBuilder()
            .setSearchText(place.name.substring(2, 5))
            .build()
        testClient.request("/searchPlaces", param2).apply {
            val result = getResult(SearchPlacesResult::class)
            Assert.assertEquals(1, result.itemsList.size)
            Assert.assertEquals(place.id, result.itemsList[0].place.id)
            Assert.assertFalse(result.itemsList[0].hasDistanceMeters())
        }
    }

    // TODO: 유저 없는 경우도 테스트?
}
