package route

import domain.accessibility.repository.BuildingAccessibilityRepository
import domain.accessibility.repository.BuildingAccessibilityUpvoteRepository
import domain.accessibility.repository.PlaceAccessibilityRepository
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.koin.test.inject
import ourMap.protocol.Common
import ourMap.protocol.Model
import ourMap.protocol.RegisterAccessibilityParams
import ourMap.protocol.RegisterAccessibilityResult

class RegisterAccessibilityTest : OurMapServerRouteTestBase() {
    private val placeAccessibilityRepository by inject<PlaceAccessibilityRepository>()
    private val buildingAccessibilityRepository by inject<BuildingAccessibilityRepository>()
    private val buildingAccessibilityUpvoteRepository by inject<BuildingAccessibilityUpvoteRepository>()

    @Before
    fun setUp() = transactionManager.doInTransaction {
        placeAccessibilityRepository.removeAll()
        buildingAccessibilityUpvoteRepository.removeAll()
        buildingAccessibilityRepository.removeAll()
    }

    @Test
    fun testRegisterAccessibility() = runRouteTest {
        repeat(3) { idx ->
            val expectedRegisteredUserOrder = idx + 1
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
                        .setEntranceStairInfo(Model.StairInfo.NONE)
                        .setHasSlope(true)
                        .setHasElevator(true)
                        .setElevatorStairInfo(Model.StairInfo.TWO_TO_FIVE)
                        .setComment(Common.StringValue.newBuilder().setValue("건물 코멘트"))
                )
                .setPlaceAccessibilityParams(
                    RegisterAccessibilityParams.RegisterPlaceAccessibilityParams.newBuilder()
                        .setPlaceId(place.id)
                        .setIsFirstFloor(false)
                        .setStairInfo(Model.StairInfo.ONE)
                        .setHasSlope(true)
                        .setComment(Common.StringValue.newBuilder().setValue("장소 코멘트"))
                )
                .build()
            testClient.request("/registerAccessibility", params).apply {
                val result = getResult(RegisterAccessibilityResult::class)
                val buildingAccessibility = result.buildingAccessibility
                Assert.assertTrue(result.hasBuildingAccessibility())
                Assert.assertEquals(place.building.id, buildingAccessibility.buildingId)
                Assert.assertEquals(Model.StairInfo.NONE, buildingAccessibility.entranceStairInfo)
                Assert.assertTrue(buildingAccessibility.hasSlope)
                Assert.assertTrue(buildingAccessibility.hasElevator)
                Assert.assertEquals(Model.StairInfo.TWO_TO_FIVE, buildingAccessibility.elevatorStairInfo)
                Assert.assertFalse(result.buildingAccessibility.isUpvoted)
                Assert.assertEquals(0, result.buildingAccessibility.totalUpvoteCount)

                Assert.assertEquals(1, result.buildingAccessibilityCommentsCount)
                Assert.assertEquals(place.building.id, result.buildingAccessibilityCommentsList[0].buildingId)
                Assert.assertEquals(user.id, result.buildingAccessibilityCommentsList[0].user.id)
                Assert.assertEquals("건물 코멘트", result.buildingAccessibilityCommentsList[0].comment)

                val placeAccessibility = result.placeAccessibility
                Assert.assertTrue(result.hasPlaceAccessibility())
                Assert.assertEquals(place.id, placeAccessibility.placeId)
                Assert.assertFalse(placeAccessibility.isFirstFloor)
                Assert.assertEquals(Model.StairInfo.ONE, placeAccessibility.stairInfo)
                Assert.assertTrue(placeAccessibility.hasSlope)

                Assert.assertEquals(1, result.placeAccessibilityCommentsCount)
                Assert.assertEquals(place.id, result.placeAccessibilityCommentsList[0].placeId)
                Assert.assertEquals(user.id, result.placeAccessibilityCommentsList[0].user.id)
                Assert.assertEquals("장소 코멘트", result.placeAccessibilityCommentsList[0].comment)

                Assert.assertEquals(expectedRegisteredUserOrder, result.registeredUserOrder)
            }
        }
    }

    // TODO: 유저 없는 경우도 테스트?
}
