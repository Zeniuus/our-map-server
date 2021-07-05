package domain.place

import domain.place.entity.Building
import domain.place.entity.BuildingAddress
import domain.place.entity.Place
import domain.place.repository.BuildingRepository
import domain.place.repository.PlaceRepository
import domain.place.service.SearchPlaceService
import domain.util.EntityIdRandomGenerator
import domain.util.Location
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class SearchPlaceServiceTest : PlaceDomainTestBase() {
    private val placeRepository = koin.get<PlaceRepository>()
    private val buildingRepository = koin.get<BuildingRepository>()
    private val searchPlaceService = koin.get<SearchPlaceService>()

    @Before
    fun setUp() {
        placeRepository.removeAll()
        buildingRepository.removeAll()
    }

    @Test
    fun `정상적인 경우`() {
        val result1 = searchPlaceService.searchPlaces("수환", Location(0.0, 0.0))
        Assert.assertEquals(0, result1.size)

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

        val result2 = searchPlaceService.searchPlaces("타워", Location(0.0, 0.0))
        Assert.assertEquals(1, result2.size)
        Assert.assertEquals(place.id, result2[0].id)
        val result3 = searchPlaceService.searchPlaces("워타", Location(0.0, 0.0))
        Assert.assertEquals(0, result3.size)
        val result4 = searchPlaceService.searchPlaces("타", Location(0.0, 0.0)) // 한글자로는 검색이 안 되어야 한다.
        Assert.assertEquals(0, result4.size)
    }
}
