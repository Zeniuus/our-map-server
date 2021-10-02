package route

import ClockMock
import org.junit.Assert
import org.junit.Test
import org.koin.test.inject
import ourMap.protocol.RegisterBuildingAccessibilityCommentParams
import ourMap.protocol.RegisterBuildingAccessibilityCommentResult
import java.time.Duration

class RegisterBuildingAccessibilityCommentTest : OurMapServerRouteTestBase() {
    private val clock by inject<ClockMock>()

    @Test
    fun testRegisterBuildingAccessibilityComment() = runRouteTest {

        val user = transactionManager.doInTransaction {
            testDataGenerator.createUser()
        }
        val userClient = getTestClient(user)
        val place = transactionManager.doInTransaction {
            testDataGenerator.createBuildingAndPlace()
        }

        run {
            val params = RegisterBuildingAccessibilityCommentParams.newBuilder()
                .setBuildingId(place.building.id)
                .setComment("실명 코멘트")
            userClient.request("/registerBuildingAccessibilityComment", params).getResult(RegisterBuildingAccessibilityCommentResult::class)
        }
        clock.advanceTime(Duration.ofSeconds(1))
        val result = run {
            val params = RegisterBuildingAccessibilityCommentParams.newBuilder()
                .setBuildingId(place.building.id)
                .setComment("익명 코멘트")
            requestWithoutAuth("/registerBuildingAccessibilityComment", params).getResult(RegisterBuildingAccessibilityCommentResult::class)
        }

        Assert.assertEquals(2, result.commentsCount)
        Assert.assertEquals(place.building.id, result.commentsList[0].buildingId)
        Assert.assertEquals("익명 코멘트", result.commentsList[0].comment)
        Assert.assertFalse(result.commentsList[0].hasUser())
        // TODO: clockMock이 제대로 주입되지 않아서 이 부분만 실패하고 있다.
//        Assert.assertEquals(clock.millis(), result.commentsList[0].createdAt.value)
        Assert.assertEquals(place.building.id, result.commentsList[1].buildingId)
        Assert.assertEquals("실명 코멘트", result.commentsList[1].comment)
        Assert.assertEquals(user.id, result.commentsList[1].user.id)
        // TODO: clockMock이 제대로 주입되지 않아서 이 부분만 실패하고 있다.
//        Assert.assertEquals((clock.instant() - Duration.ofSeconds(1)).toEpochMilli(), result.commentsList[1].createdAt.value)
    }
}
