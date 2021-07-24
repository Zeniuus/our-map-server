package route

import org.junit.Assert
import org.junit.Test
import ourMap.protocol.Model
import ourMap.protocol.SearchPlacesParams
import ourMap.protocol.SearchPlacesResult
import kotlin.random.Random

class PlaceRouteTest : OurMapServerRouteTestBase() {
    @Test
    fun testSearchPlaces() = runRouteTest {
        val user = transactionManager.doInTransaction {
            testDataGenerator.createUser()
        }
        val testClient = getTestClient(user)
        val place = transactionManager.doInTransaction {
            testDataGenerator.createPlace(placeName = Random.nextBytes(32).toString())
        }

        val params = SearchPlacesParams.newBuilder()
            .setSearchText(place.name.substring(2, 5))
            .setCurrentLocation(Model.Location.newBuilder().setLng(place.lng).setLat(place.lat))
            .build()
        testClient.request("/searchPlaces", params).apply {
            val result = getResult(SearchPlacesResult::class)
            Assert.assertEquals(1, result.itemsList.size)
            Assert.assertEquals(place.id, result.itemsList[0].place.id)
        }
    }
}
