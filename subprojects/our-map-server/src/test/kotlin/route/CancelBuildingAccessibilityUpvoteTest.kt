package route

import domain.accessibility.service.BuildingAccessibilityUpvoteService
import org.junit.Assert
import org.junit.Test
import org.koin.test.inject
import ourMap.protocol.CancelBuildingAccessibilityUpvoteParams
import ourMap.protocol.GiveBuildingAccessibilityUpvoteParams

class CancelBuildingAccessibilityUpvoteTest : OurMapServerRouteTestBase() {
    private val buildingAccessibilityUpvoteService by inject<BuildingAccessibilityUpvoteService>()

    @Test
    fun cancelBuildingAccessibilityUpvoteTest() = runRouteTest {
        val (user, buildingAccessibility) = transactionManager.doInTransaction {
            val user = testDataGenerator.createUser()
            val place = testDataGenerator.createBuildingAndPlace()
            val buildingAccessibility = testDataGenerator.registerBuildingAndPlaceAccessibility(user = user, place = place).second
            Pair(user, buildingAccessibility)
        }

        val testClient = getTestClient(user)
        val giveUpvoteParams = GiveBuildingAccessibilityUpvoteParams.newBuilder()
            .setBuildingAccessibilityId(buildingAccessibility.id)
            .build()
        testClient.request("/giveBuildingAccessibilityUpvote", giveUpvoteParams)

        val cancelUpvoteParams = CancelBuildingAccessibilityUpvoteParams.newBuilder()
            .setBuildingAccessibilityId(buildingAccessibility.id)
            .build()
        testClient.request("/cancelBuildingAccessibilityUpvote", cancelUpvoteParams)
        transactionManager.doInTransaction {
            Assert.assertFalse(buildingAccessibilityUpvoteService.isUpvoted(user, buildingAccessibility))
        }
    }
}
