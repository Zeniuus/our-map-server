import domain.accessibility.accessibilityDomainModule
import domain.accessibility.entity.BuildingAccessibility
import domain.accessibility.entity.BuildingAccessibilityComment
import domain.accessibility.entity.PlaceAccessibility
import domain.accessibility.entity.PlaceAccessibilityComment
import domain.accessibility.entity.StairInfo
import domain.accessibility.service.BuildingAccessibilityCommentService
import domain.accessibility.service.BuildingAccessibilityService
import domain.accessibility.service.BuildingAccessibilityUpvoteService
import domain.accessibility.service.PlaceAccessibilityCommentService
import domain.accessibility.service.PlaceAccessibilityService
import domain.place.entity.Building
import domain.place.entity.BuildingAddress
import domain.place.entity.Place
import domain.place.placeDomainModule
import domain.place.repository.BuildingRepository
import domain.place.repository.PlaceRepository
import domain.user.entity.User
import domain.user.service.UserService
import domain.user.userDomainModule
import domain.util.EntityIdGenerator
import domain.util.Location
import domain.village.entity.Village
import domain.village.repository.EupMyeonDongRepository
import domain.village.repository.SiGunGuRepository
import domain.village.repository.VillageRepository
import domain.village.service.UserFavoriteVillageService
import domain.village.villageDomainModule
import java.util.Base64
import kotlin.random.Random

class TestDataGenerator {
    private val koin = OurMapIoCFactory.createScopedContainer {
        modules(
            userDomainModule,
            placeDomainModule,
            accessibilityDomainModule,
            villageDomainModule,
        )
    }

    private val userService = koin.get<UserService>()
    private val placeRepository = koin.get<PlaceRepository>()
    private val buildingRepository = koin.get<BuildingRepository>()
    private val placeAccessibilityService = koin.get<PlaceAccessibilityService>()
    private val buildingAccessibilityService = koin.get<BuildingAccessibilityService>()
    private val eupMyeonDongRepository = koin.get<EupMyeonDongRepository>()
    private val siGunGuRepository = koin.get<SiGunGuRepository>()
    private val userFavoriteVillageService = koin.get<UserFavoriteVillageService>()
    private val buildingAccessibilityUpvoteService = koin.get<BuildingAccessibilityUpvoteService>()
    private val villageRepository = koin.get<VillageRepository>()
    private val buildingAccessibilityCommentService = koin.get<BuildingAccessibilityCommentService>()
    private val placeAccessibilityCommentService = koin.get<PlaceAccessibilityCommentService>()

    fun createUser(
        nickname: String = generateRandomString(12),
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
        building: Building,
    ): Place {
        return placeRepository.add(Place(
            id = EntityIdGenerator.generateRandom(),
            name = placeName,
            lng = building.location.lng,
            lat = building.location.lat,
            building = building,
            siGunGuId = building.siGunGuId,
            eupMyeonDongId = building.eupMyeonDongId,
        ))
    }

    fun createBuildingAndPlace(
        placeName: String = "장소장소",
        location: Location = Location(127.5, 37.5),
        building: Building? = null,
        eupMyeonDongId: String = eupMyeonDongRepository.listAll()[0].id,
        siGunGuId: String = siGunGuRepository.listAll()[0].id
    ): Place {
        val buildingToUse = building ?: buildingRepository.add(Building(
            id = EntityIdGenerator.generateRandom(),
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
            siGunGuId = siGunGuId,
            eupMyeonDongId = eupMyeonDongId,
        ))
        return createPlace(placeName, buildingToUse)
    }

    fun registerBuildingAndPlaceAccessibility(place: Place, user: User? = null): Pair<PlaceAccessibility, BuildingAccessibility> {
        val placeAccessibility = placeAccessibilityService.create(
            PlaceAccessibilityService.CreateParams(
                placeId = place.id,
                isFirstFloor = true,
                stairInfo = StairInfo.NONE,
                hasSlope = true,
                userId = user?.id,
            )
        )
        val buildingAccessibility = buildingAccessibilityService.create(
            BuildingAccessibilityService.CreateParams(
                buildingId = place.building.id,
                entranceStairInfo = StairInfo.NONE,
                hasSlope = true,
                hasElevator = true,
                elevatorStairInfo = StairInfo.NONE,
                userId = user?.id,
            )
        )
        return Pair(placeAccessibility, buildingAccessibility)
    }

    fun registerBuildingAccessibilityComment(building: Building, comment: String, user: User? = null): BuildingAccessibilityComment {
        return buildingAccessibilityCommentService.create(
            BuildingAccessibilityCommentService.CreateParams(
            buildingId = building.id,
            userId = user?.id,
            comment = comment,
        ))
    }

    fun registerPlaceAccessibilityComment(place: Place, comment: String, user: User? = null): PlaceAccessibilityComment {
        return placeAccessibilityCommentService.create(PlaceAccessibilityCommentService.CreateParams(
            placeId = place.id,
            userId = user?.id,
            comment = comment,
        ))
    }

    fun getRandomVillage(): Village {
        return villageRepository.listAll().shuffled()[0]
    }

    fun registerFavoriteVillage(user: User, village: Village) {
        userFavoriteVillageService.register(user, village)
    }

    fun giveBuildingAccessibilityUpvote(buildingAccessibility: BuildingAccessibility, user: User = createUser()) {
        buildingAccessibilityUpvoteService.giveUpvote(user, buildingAccessibility)
    }

    private fun generateRandomString(length: Int): String {
        return Base64.getEncoder().encodeToString(Random.nextBytes(length)).take(length)
    }
}
