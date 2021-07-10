package domain.placeAccessibility

import domain.place.entity.Building
import domain.place.entity.BuildingAddress
import domain.place.entity.Place
import domain.place.repository.BuildingRepository
import domain.place.repository.PlaceRepository
import domain.placeAccessibility.entity.BuildingAccessibility
import domain.placeAccessibility.entity.BuildingStairInfo
import domain.placeAccessibility.entity.PlaceAccessibility
import domain.placeAccessibility.repository.BuildingAccessibilityRepository
import domain.placeAccessibility.repository.PlaceAccessibilityRepository
import domain.placeAccessibility.service.SearchPlaceAccessibilityService
import domain.util.EntityIdRandomGenerator
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.koin.test.inject

class SearchPlaceAccessibilityServiceTest : PlaceAccessibilityDomainTestBase() {
    private val placeRepository by inject<PlaceRepository>()
    private val buildingRepository by inject<BuildingRepository>()
    private val placeAccessibilityRepository by inject<PlaceAccessibilityRepository>()
    private val buildingAccessibilityRepository by inject<BuildingAccessibilityRepository>()
    private val searchPlaceAccessibilityService by inject<SearchPlaceAccessibilityService>()

    @Before
    fun setUp() = transactionManager.doInTransaction {
        placeAccessibilityRepository.removeAll()
        buildingAccessibilityRepository.removeAll()
        placeRepository.removeAll()
        buildingRepository.removeAll()
    }

    @Test
    fun `정상적인 경우`() = transactionManager.doInTransaction {
        val building = buildingRepository.add(Building(
            id = EntityIdRandomGenerator.generate(),
            name = "아크로서울포레스트 D타워",
            lng = 0.0,
            lat = 0.0,
            address = BuildingAddress(
                siDo = "서울특별시",
                siGunGu = "성동구",
                eupMyeonDong = "성수동",
                li = "",
                roadName = "왕십리로",
                mainBuildingNumber = "83",
                subBuildingNumber = "21"
            )
        ))
        val place = placeRepository.add(Place(
            id = EntityIdRandomGenerator.generate(),
            name = "D타워 장소1",
            lng = 0.0,
            lat = 0.0,
            building = building
        ))

        val result1 = searchPlaceAccessibilityService.search(listOf(place)).getAccessibility(place)
        Assert.assertNull(result1.first)
        Assert.assertNull(result1.second)

        val buildingAccessibility = buildingAccessibilityRepository.add(BuildingAccessibility(
            id = EntityIdRandomGenerator.generate(),
            buildingId = building.id,
            hasElevator = true,
            hasObsticleToElevator = true,
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
