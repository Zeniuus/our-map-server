package domain.accessibility

import domain.DomainTestBase
import domain.accessibility.entity.BuildingAccessibility
import domain.accessibility.entity.BuildingStairInfo
import domain.accessibility.entity.PlaceAccessibility
import domain.accessibility.repository.BuildingAccessibilityRepository
import domain.accessibility.repository.PlaceAccessibilityRepository
import domain.accessibility.service.SearchAccessibilityService
import domain.util.EntityIdGenerator
import org.junit.Assert
import org.junit.Test
import org.koin.test.inject
import java.time.Clock

class SearchPlaceAccessibilityServiceTest : DomainTestBase() {
    override val koinModules = listOf(accessibilityDomainModule)

    private val clock by inject<Clock>()
    private val placeAccessibilityRepository by inject<PlaceAccessibilityRepository>()
    private val buildingAccessibilityRepository by inject<BuildingAccessibilityRepository>()
    private val searchAccessibilityService by inject<SearchAccessibilityService>()

    @Test
    fun `정상적인 경우`() = transactionManager.doAndRollback {
        val place = testDataGenerator.createBuildingAndPlace(placeName = "D타워 장소1")

        val result1 = searchAccessibilityService.search(listOf(place)).getAccessibility(place)
        Assert.assertNull(result1.first)
        Assert.assertNull(result1.second)

        val buildingAccessibility = buildingAccessibilityRepository.add(BuildingAccessibility(
            id = EntityIdGenerator.generateRandom(),
            buildingId = place.building.id,
            entranceStairInfo = BuildingStairInfo.NONE,
            hasSlope = true,
            hasElevator = true,
            elevatorStairInfo = BuildingStairInfo.NONE,
            userId = null,
            createdAt = clock.instant(),
        ))

        val result2 = searchAccessibilityService.search(listOf(place)).getAccessibility(place)
        Assert.assertNull(result2.first)
        Assert.assertNotNull(result2.second)
        Assert.assertEquals(buildingAccessibility.id, result2.second!!.id)

        val placeAccessibility = placeAccessibilityRepository.add(PlaceAccessibility(
            id = EntityIdGenerator.generateRandom(),
            placeId = place.id,
            isFirstFloor = false,
            hasStair = false,
            hasSlope = true,
            userId = null,
            createdAt = clock.instant(),
        ))
        val result3 = searchAccessibilityService.search(listOf(place)).getAccessibility(place)
        Assert.assertNotNull(result3.first)
        Assert.assertEquals(placeAccessibility.id, result3.first!!.id)
        Assert.assertNotNull(result3.second)
        Assert.assertEquals(buildingAccessibility.id, result3.second!!.id)
    }
}
