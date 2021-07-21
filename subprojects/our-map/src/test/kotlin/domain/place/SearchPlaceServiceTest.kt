package domain.place

import TestDataGenerator
import domain.place.service.SearchPlaceService
import domain.util.Location
import org.junit.Assert
import org.junit.Test
import org.koin.test.inject

class SearchPlaceServiceTest : PlaceDomainTestBase() {
    private val testDataGenerator = TestDataGenerator()
    private val searchPlaceService by inject<SearchPlaceService>()

    @Test
    fun `검색이 잘 되어야 한다`() = transactionManager.doAndRollback {
        val result1 = searchPlaceService.searchPlaces("수환", Location(0.0, 0.0))
        Assert.assertEquals(0, result1.size)

        val place = testDataGenerator.createPlace(placeName = "D타워 장소1")

        val result2 = searchPlaceService.searchPlaces("타워", Location(0.0, 0.0))
        Assert.assertEquals(1, result2.size)
        Assert.assertEquals(place.id, result2[0].id)
        val result3 = searchPlaceService.searchPlaces("워타", Location(0.0, 0.0))
        Assert.assertEquals(0, result3.size)
        val result4 = searchPlaceService.searchPlaces("타", Location(0.0, 0.0)) // 한글자로는 검색이 안 되어야 한다.
        Assert.assertEquals(0, result4.size)
    }

    @Test
    fun `가까운 장소가 먼저 노출되어야 한다`() = transactionManager.doAndRollback {
        val result1 = searchPlaceService.searchPlaces("수환", Location(0.0, 0.0))
        Assert.assertEquals(0, result1.size)

        val nearPlace = testDataGenerator.createPlace(
            placeName = "D타워 장소1",
            location = Location(0.05, 0.05)
        )
        val farPlace = testDataGenerator.createPlace(
            placeName = "D타워 장소2",
            location = Location(0.1, 0.1)
        )

        val result = searchPlaceService.searchPlaces("D타워", Location(0.0, 0.0))
        Assert.assertEquals(2, result.size)
        Assert.assertEquals(nearPlace.id, result[0].id)
        Assert.assertEquals(farPlace.id, result[1].id)
    }
}
