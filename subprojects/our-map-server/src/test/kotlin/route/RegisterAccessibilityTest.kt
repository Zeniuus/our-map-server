package route

import org.junit.Assert
import org.junit.Test
import ourMap.protocol.Model
import ourMap.protocol.RegisterAccessibilityParams
import ourMap.protocol.RegisterAccessibilityResult

class RegisterAccessibilityTest : OurMapServerRouteTestBase() {
    @Test
    fun testRegisterAccessibility() = runRouteTest {
        val user = transactionManager.doInTransaction {
            testDataGenerator.createUser()
        }
        val testClient = getTestClient(user)
        val place = transactionManager.doInTransaction {
            testDataGenerator.createBuildingAndPlace(placeName = "장소장소")
        }

        val params = RegisterAccessibilityParams.newBuilder()
            .setBuildingAccessibilityParams(
                RegisterAccessibilityParams.RegisterBuildingAccessibilityParams.newBuilder()
                    .setBuildingId(place.building.id)
                    .setHasElevator(true)
                    .setHasObstacleToElevator(true)
                    .setStairInfo(Model.BuildingAccessibility.StairInfo.LESS_THAN_FIVE)
            )
            .setPlaceAccessibilityParams(
                RegisterAccessibilityParams.RegisterPlaceAccessibilityParams.newBuilder()
                    .setPlaceId(place.id)
                    .setIsFirstFloor(false)
                    .setHasStair(false)
                    .setIsWheelchairAccessible(true)
            )
            .build()
        testClient.request("/registerAccessibility", params).apply {
            val result = getResult(RegisterAccessibilityResult::class)
            val buildingAccessibility = result.buildingAccessibility
            Assert.assertTrue(result.hasBuildingAccessibility())
            Assert.assertEquals(place.building.id, buildingAccessibility.buildingId)
            Assert.assertTrue(buildingAccessibility.hasElevator)
            Assert.assertTrue(buildingAccessibility.hasObstacleToElevator)
            Assert.assertEquals(Model.BuildingAccessibility.StairInfo.LESS_THAN_FIVE, buildingAccessibility.stairInfo)
            Assert.assertFalse(result.buildingAccessibility.isUpvoted)
            Assert.assertEquals(0, result.buildingAccessibility.totalUpvoteCount)

            val placeAccessibility = result.placeAccessibility
            Assert.assertTrue(result.hasPlaceAccessibility())
            Assert.assertEquals(place.id, placeAccessibility.placeId)
            Assert.assertFalse(placeAccessibility.isFirstFloor)
            Assert.assertFalse(placeAccessibility.hasStair)
            Assert.assertTrue(placeAccessibility.isWheelchairAccessible)
        }
    }
}
