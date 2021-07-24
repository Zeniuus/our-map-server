package route

import converter.BuildingAccessibilityConverter
import org.junit.Assert
import org.junit.Test
import ourMap.protocol.GetAccessibilityParams
import ourMap.protocol.GetAccessibilityResult

class GetAccessibilityTest : OurMapServerRouteTestBase() {
    @Test
    fun getAccessibilityTest() = runRouteTest {
        val user = transactionManager.doInTransaction {
            testDataGenerator.createUser()
        }
        val testClient = getTestClient(user)
        val (place, placeAccessibility, buildingAccessibility) = transactionManager.doInTransaction {
            val place = testDataGenerator.createPlace(placeName = "장소장소")
            val (placeAccessibility, buildingAccessibility) = testDataGenerator.registerPlaceAccessibility(place, user)
            Triple(place, placeAccessibility, buildingAccessibility)
        }

        val params = GetAccessibilityParams.newBuilder()
            .setPlaceId(place.id)
            .build()
        testClient.request("/getAccessibility", params).apply {
            val result = getResult(GetAccessibilityResult::class)
            Assert.assertEquals(buildingAccessibility.id, result.buildingAccessibility.id)
            Assert.assertEquals(buildingAccessibility.buildingId, result.buildingAccessibility.buildingId)
            Assert.assertEquals(buildingAccessibility.hasElevator, result.buildingAccessibility.hasElevator)
            Assert.assertEquals(buildingAccessibility.hasObstacleToElevator, result.buildingAccessibility.hasObstacleToElevator)
            Assert.assertEquals(buildingAccessibility.stairInfo, BuildingAccessibilityConverter.fromProto(result.buildingAccessibility.stairInfo))
            Assert.assertEquals(user.nickname, result.buildingAccessibility.registeredUserName.value)

            Assert.assertEquals(placeAccessibility.id, result.placeAccessibility.id)
            Assert.assertEquals(placeAccessibility.placeId, result.placeAccessibility.placeId)
            Assert.assertEquals(placeAccessibility.isFirstFloor, result.placeAccessibility.isFirstFloor)
            Assert.assertEquals(placeAccessibility.hasStair, result.placeAccessibility.hasStair)
            Assert.assertEquals(placeAccessibility.isWheelchairAccessible, result.placeAccessibility.isWheelchairAccessible)
            Assert.assertEquals(user.nickname, result.placeAccessibility.registeredUserName.value)
        }
    }
}
