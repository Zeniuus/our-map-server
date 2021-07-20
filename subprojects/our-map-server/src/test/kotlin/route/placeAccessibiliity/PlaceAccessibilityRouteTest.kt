package route.placeAccessibiliity

import org.junit.Assert
import org.junit.Test
import ourMap.protocol.GetAccessibilityParams
import ourMap.protocol.GetAccessibilityResult
import ourMap.protocol.Model
import ourMap.protocol.RegisterAccessibilityParams
import ourMap.protocol.RegisterAccessibilityResult
import route.RouteTestBase
import route.converter.BuildingAccessibilityConverter

class PlaceAccessibilityRouteTest : RouteTestBase() {
    @Test
    fun testRegisterAccessibility() = runRouteTest {
        val user = transactionManager.doInTransaction {
            testDataGenerator.createUser()
        }
        val testClient = getTestClient(user)
        val place = transactionManager.doInTransaction {
            testDataGenerator.createPlace(placeName = "장소장소")
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

            val placeAccessibility = result.placeAccessibility
            Assert.assertTrue(result.hasPlaceAccessibility())
            Assert.assertEquals(place.id, placeAccessibility.placeId)
            Assert.assertFalse(placeAccessibility.isFirstFloor)
            Assert.assertFalse(placeAccessibility.hasStair)
            Assert.assertTrue(placeAccessibility.isWheelchairAccessible)
        }
    }

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
