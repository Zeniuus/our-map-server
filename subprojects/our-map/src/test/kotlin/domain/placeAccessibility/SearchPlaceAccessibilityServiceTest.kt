package domain.placeAccessibility

import domain.DomainTestBase
import domain.placeAccessibility.entity.BuildingAccessibility
import domain.placeAccessibility.entity.BuildingStairInfo
import domain.placeAccessibility.entity.PlaceAccessibility
import domain.placeAccessibility.repository.BuildingAccessibilityRepository
import domain.placeAccessibility.repository.PlaceAccessibilityRepository
import domain.placeAccessibility.service.SearchPlaceAccessibilityService
import domain.util.EntityIdRandomGenerator
import org.junit.Assert
import org.junit.Test
import org.koin.test.inject

class SearchPlaceAccessibilityServiceTest : DomainTestBase() {
    override val koinModules = listOf(placeAccessibilityDomainModule)

    private val placeAccessibilityRepository by inject<PlaceAccessibilityRepository>()
    private val buildingAccessibilityRepository by inject<BuildingAccessibilityRepository>()
    private val searchPlaceAccessibilityService by inject<SearchPlaceAccessibilityService>()

    @Test
    fun `정상적인 경우`() = transactionManager.doAndRollback {
        val place = testDataGenerator.createPlace(placeName = "D타워 장소1")

        val result1 = searchPlaceAccessibilityService.search(listOf(place)).getAccessibility(place)
        Assert.assertNull(result1.first)
        Assert.assertNull(result1.second)

        val buildingAccessibility = buildingAccessibilityRepository.add(BuildingAccessibility(
            id = EntityIdRandomGenerator.generate(),
            buildingId = place.building.id,
            hasElevator = true,
            hasObstacleToElevator = true,
            stairInfo = BuildingStairInfo.LESS_THAN_FIVE,
            userId = null
        ))

        val result2 = searchPlaceAccessibilityService.search(listOf(place)).getAccessibility(place)
        Assert.assertNull(result2.first)
        Assert.assertNotNull(result2.second)
        Assert.assertEquals(buildingAccessibility.id, result2.second!!.id)

        val placeAccessibility = placeAccessibilityRepository.add(PlaceAccessibility(
            id = EntityIdRandomGenerator.generate(),
            placeId = place.id,
            isFirstFloor = false,
            hasStair = false,
            isWheelchairAccessible = true,
            userId = null
        ))
        val result3 = searchPlaceAccessibilityService.search(listOf(place)).getAccessibility(place)
        Assert.assertNotNull(result3.first)
        Assert.assertEquals(placeAccessibility.id, result3.first!!.id)
        Assert.assertNotNull(result3.second)
        Assert.assertEquals(buildingAccessibility.id, result3.second!!.id)
    }
}
