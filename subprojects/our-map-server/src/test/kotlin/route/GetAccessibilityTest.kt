package route

import converter.StairInfoConverter
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
            val place = testDataGenerator.createBuildingAndPlace(placeName = "장소장소")
            val (placeAccessibility, buildingAccessibility) = testDataGenerator.registerBuildingAndPlaceAccessibility(place, user)

            repeat(2) {
                testDataGenerator.giveBuildingAccessibilityUpvote(buildingAccessibility)
            }
            testDataGenerator.giveBuildingAccessibilityUpvote(buildingAccessibility, user)

            Triple(place, placeAccessibility, buildingAccessibility)
        }

        val params = GetAccessibilityParams.newBuilder()
            .setPlaceId(place.id)
            .build()
        testClient.request("/getAccessibility", params).apply {
            val result = getResult(GetAccessibilityResult::class)
            Assert.assertEquals(buildingAccessibility.id, result.buildingAccessibility.id)
            Assert.assertEquals(buildingAccessibility.buildingId, result.buildingAccessibility.buildingId)
            Assert.assertEquals(buildingAccessibility.entranceStairInfo, StairInfoConverter.fromProto(result.buildingAccessibility.entranceStairInfo))
            Assert.assertEquals(buildingAccessibility.hasSlope, result.buildingAccessibility.hasSlope)
            Assert.assertEquals(buildingAccessibility.hasElevator, result.buildingAccessibility.hasElevator)
            Assert.assertEquals(buildingAccessibility.elevatorStairInfo, StairInfoConverter.fromProto(result.buildingAccessibility.elevatorStairInfo))
            Assert.assertEquals(user.nickname, result.buildingAccessibility.registeredUserName.value)
            Assert.assertTrue(result.buildingAccessibility.isUpvoted)
            Assert.assertEquals(3, result.buildingAccessibility.totalUpvoteCount)

            Assert.assertEquals(placeAccessibility.id, result.placeAccessibility.id)
            Assert.assertEquals(placeAccessibility.placeId, result.placeAccessibility.placeId)
            Assert.assertEquals(placeAccessibility.isFirstFloor, result.placeAccessibility.isFirstFloor)
            Assert.assertEquals(placeAccessibility.stairInfo, StairInfoConverter.fromProto(result.placeAccessibility.stairInfo))
            Assert.assertEquals(placeAccessibility.hasSlope, result.placeAccessibility.hasSlope)
            Assert.assertEquals(user.nickname, result.placeAccessibility.registeredUserName.value)
        }
    }

    // TODO: 유저 없는 경우도 테스트?
}
