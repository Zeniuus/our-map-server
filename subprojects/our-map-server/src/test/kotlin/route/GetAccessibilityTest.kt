package route

import converter.StairInfoConverter
import domain.accessibility.entity.BuildingAccessibility
import domain.accessibility.entity.BuildingAccessibilityComment
import domain.accessibility.entity.PlaceAccessibility
import domain.accessibility.entity.PlaceAccessibilityComment
import domain.place.entity.Place
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
        val (place, placeAccessibility, buildingAccessibility, placeAccessibilityComment, buildingAccessibilityComment) = transactionManager.doInTransaction {
            val place = testDataGenerator.createBuildingAndPlace(placeName = "장소장소")
            val (placeAccessibility, buildingAccessibility) = testDataGenerator.registerBuildingAndPlaceAccessibility(place, user)

            repeat(2) {
                testDataGenerator.giveBuildingAccessibilityUpvote(buildingAccessibility)
            }
            testDataGenerator.giveBuildingAccessibilityUpvote(buildingAccessibility, user)

            val buildingAccessibilityComment = testDataGenerator.registerBuildingAccessibilityComment(place.building, "건물 코멘트")
            val placeAccessibilityComment = testDataGenerator.registerPlaceAccessibilityComment(place, "장소 코멘트", user)

            data class Result(
                val place: Place,
                val placeAccessibility: PlaceAccessibility,
                val buildingAccessibility: BuildingAccessibility,
                val placeAccessibilityComment: PlaceAccessibilityComment,
                val buildingAccessibilityComment: BuildingAccessibilityComment,
            )
            Result(place, placeAccessibility, buildingAccessibility, placeAccessibilityComment, buildingAccessibilityComment)
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
            Assert.assertEquals(1, result.buildingAccessibilityCommentsCount)
            Assert.assertEquals(buildingAccessibilityComment.id, result.buildingAccessibilityCommentsList[0].id)
            Assert.assertEquals(buildingAccessibilityComment.buildingId, result.buildingAccessibilityCommentsList[0].buildingId)
            Assert.assertFalse(result.buildingAccessibilityCommentsList[0].hasUser())
            Assert.assertEquals(buildingAccessibilityComment.comment, result.buildingAccessibilityCommentsList[0].comment)
            Assert.assertEquals(buildingAccessibilityComment.createdAt.toEpochMilli(), result.buildingAccessibilityCommentsList[0].createdAt.value)

            Assert.assertEquals(placeAccessibility.id, result.placeAccessibility.id)
            Assert.assertEquals(placeAccessibility.placeId, result.placeAccessibility.placeId)
            Assert.assertEquals(placeAccessibility.isFirstFloor, result.placeAccessibility.isFirstFloor)
            Assert.assertEquals(placeAccessibility.stairInfo, StairInfoConverter.fromProto(result.placeAccessibility.stairInfo))
            Assert.assertEquals(placeAccessibility.hasSlope, result.placeAccessibility.hasSlope)
            Assert.assertEquals(user.nickname, result.placeAccessibility.registeredUserName.value)
            Assert.assertEquals(1, result.placeAccessibilityCommentsCount)
            Assert.assertEquals(placeAccessibilityComment.id, result.placeAccessibilityCommentsList[0].id)
            Assert.assertEquals(placeAccessibilityComment.placeId, result.placeAccessibilityCommentsList[0].placeId)
            Assert.assertTrue(result.placeAccessibilityCommentsList[0].hasUser())
            Assert.assertEquals(placeAccessibilityComment.comment, result.placeAccessibilityCommentsList[0].comment)
            Assert.assertEquals(placeAccessibilityComment.createdAt.toEpochMilli(), result.placeAccessibilityCommentsList[0].createdAt.value)
        }
    }

    // TODO: 유저 없는 경우도 테스트?
}
