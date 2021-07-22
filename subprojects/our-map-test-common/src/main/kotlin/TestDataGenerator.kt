import domain.place.entity.Building
import domain.place.entity.BuildingAddress
import domain.place.entity.Place
import domain.place.placeDomainModule
import domain.place.repository.BuildingRepository
import domain.place.repository.PlaceRepository
import domain.placeAccessibility.entity.BuildingAccessibility
import domain.placeAccessibility.entity.BuildingStairInfo
import domain.placeAccessibility.entity.PlaceAccessibility
import domain.placeAccessibility.placeAccessibilityDomainModule
import domain.placeAccessibility.service.BuildingAccessibilityService
import domain.placeAccessibility.service.PlaceAccessibilityService
import domain.user.entity.User
import domain.user.service.UserService
import domain.user.userDomainModule
import domain.util.EntityIdRandomGenerator
import domain.util.Location
import kotlin.random.Random

class TestDataGenerator {
    private val koin = OurMapIoCFactory.createScopedContainer {
        modules(
            userDomainModule,
            placeDomainModule,
            placeAccessibilityDomainModule
        )
    }

    private val userService = koin.get<UserService>()
    private val placeRepository = koin.get<PlaceRepository>()
    private val buildingRepository = koin.get<BuildingRepository>()
    private val placeAccessibilityService = koin.get<PlaceAccessibilityService>()
    private val buildingAccessibilityService = koin.get<BuildingAccessibilityService>()

    fun createUser(
        nickname: String = Random.nextBytes(32).toString(),
        password: String = "password",
        instagramId: String? = null
    ): User {
        return userService.createUser(
            UserService.CreateUserParams(
                nickname = nickname,
                password = password,
                instagramId = instagramId,
            )
        )
    }

    fun createPlace(
        placeName: String = "장소장소",
        location: Location = Location(127.5, 37.5)
    ): Place {
        val building = buildingRepository.add(Building(
            id = EntityIdRandomGenerator.generate(),
            name = "건물건물",
            lng = location.lng,
            lat = location.lat,
            address = BuildingAddress(
                siDo = "서울특별시",
                siGunGu = "성동구",
                eupMyeonDong = "성수동",
                li = "",
                roadName = "왕십리로",
                mainBuildingNumber = "83",
                subBuildingNumber = "21",
            ),
            siGunGuId = "1",
            eupMyeonDongId = "1",
        ))
        return placeRepository.add(Place(
            id = EntityIdRandomGenerator.generate(),
            name = placeName,
            lng = location.lng,
            lat = location.lat,
            building = building,
            siGunGuId = "1",
            eupMyeonDongId = "1",
        ))
    }

    fun registerPlaceAccessibility(place: Place, user: User? = null): Pair<PlaceAccessibility, BuildingAccessibility> {
        val placeAccessibility = placeAccessibilityService.create(
            PlaceAccessibilityService.CreateParams(
                placeId = place.id,
                isFirstFloor = true,
                hasStair = true,
                isWheelchairAccessible = true,
                userId = user?.id,
            )
        )
        val buildingAccessibility = buildingAccessibilityService.create(
            BuildingAccessibilityService.CreateParams(
                buildingId = place.building.id,
                hasElevator = true,
                hasObstacleToElevator = true,
                stairInfo = BuildingStairInfo.NONE,
                userId = user?.id,
            )
        )
        return Pair(placeAccessibility, buildingAccessibility)
    }
}
