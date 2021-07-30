package route

import domain.placeAccessibility.service.BuildingAccessibilityUpvoteService
import org.junit.Assert
import org.junit.Test
import org.koin.test.inject
import ourMap.protocol.GiveBuildingAccessibilityUpvoteParams

class GiveBuildingAccessibilityUpvoteTest : OurMapServerRouteTestBase() {
    private val buildingAccessibilityUpvoteService by inject<BuildingAccessibilityUpvoteService>()

    @Test
    fun giveBuildingAccessibilityUpvoteTest() = runRouteTest {
        val (user, buildingAccessibility) = transactionManager.doInTransaction {
            val user = testDataGenerator.createUser()
            val place = testDataGenerator.createPlace()
            val buildingAccessibility = testDataGenerator.registerPlaceAccessibility(user = user, place = place).second
            Pair(user, buildingAccessibility)
        }

        val testClient = getTestClient(user)
        val params = GiveBuildingAccessibilityUpvoteParams.newBuilder()
            .setBuildingAccessibilityId(buildingAccessibility.id)
            .build()
        testClient.request("/giveBuildingAccessibilityUpvote", params)
        transactionManager.doInTransaction {
            Assert.assertTrue(buildingAccessibilityUpvoteService.isUpvoted(user, buildingAccessibility))
        }
    }
}
