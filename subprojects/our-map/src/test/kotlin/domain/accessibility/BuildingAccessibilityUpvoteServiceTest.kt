package domain.accessibility

import domain.DomainTestBase
import domain.accessibility.service.BuildingAccessibilityUpvoteService
import org.junit.Assert
import org.junit.Test
import org.koin.test.inject

class BuildingAccessibilityUpvoteServiceTest : DomainTestBase() {
    override val koinModules = listOf(accessibilityDomainModule)

    private val buildingAccessibilityUpvoteService by inject<BuildingAccessibilityUpvoteService>()

    @Test
    fun `정상적인 경우`() = transactionManager.doAndRollback {
        val user = testDataGenerator.createUser()
        val place = testDataGenerator.createBuildingAndPlace()
        val buildingAccessibility = testDataGenerator.registerBuildingAndPlaceAccessibility(user = user, place = place).second

        Assert.assertFalse(buildingAccessibilityUpvoteService.isUpvoted(user, buildingAccessibility))

        // 멱등성 테스트
        val userFavoriteVillages = (1..5).map {
            buildingAccessibilityUpvoteService.giveUpvote(user, buildingAccessibility)
        }
        Assert.assertEquals(1, userFavoriteVillages.map { it.id }.toSet().size)

        Assert.assertTrue(buildingAccessibilityUpvoteService.isUpvoted(user, buildingAccessibility))

        // 멱등성 테스트
        repeat(5) {
            buildingAccessibilityUpvoteService.cancelUpvote(user, buildingAccessibility)
        }
        Assert.assertFalse(buildingAccessibilityUpvoteService.isUpvoted(user, buildingAccessibility))
    }
}
