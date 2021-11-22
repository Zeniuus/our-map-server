package route

import application.TransactionManager
import auth.UserAuthenticator
import converter.UserConverter
import converter.VillageConverter
import domain.accessibility.repository.BuildingAccessibilityRepository
import domain.accessibility.repository.PlaceAccessibilityRepository
import domain.accessibility.service.ConquerRankingService
import domain.user.repository.UserRepository
import domain.village.repository.UserFavoriteVillageRepository
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import org.koin.core.context.GlobalContext
import ourMap.protocol.Common
import ourMap.protocol.GetMyPageViewDataResult

fun Route.getMyPageViewData() {
    val koin = GlobalContext.get()

    val transactionManager = koin.get<TransactionManager>()
    val userAuthenticator = koin.get<UserAuthenticator>()
    val userRepository = koin.get<UserRepository>()
    val placeAccessibilityRepository = koin.get<PlaceAccessibilityRepository>()
    val buildingAccessibilityRepository = koin.get<BuildingAccessibilityRepository>()
    val userFavoriteVillageRepository = koin.get<UserFavoriteVillageRepository>()
    val conquerRankingService = koin.get<ConquerRankingService>()
    val villageConverter = koin.get<VillageConverter>()

    post("/getMyPageViewData") {
        val userId = userAuthenticator.checkAuth(call.request)

        call.respond(
            transactionManager.doInTransaction {
                val user = userRepository.findById(userId)
                val favoriteVillages = userFavoriteVillageRepository.findByUserAndNotDeleted(user)
                val registeredPlaceAccessibilityCount = placeAccessibilityRepository.countByUserId(userId)
                val registeredBuildingAccessibilityCount = buildingAccessibilityRepository.countByUserId(userId)
                GetMyPageViewDataResult.newBuilder()
                    .setUser(UserConverter.toProto(user))
                    .addAllFavoriteVillages(
                        favoriteVillages.map { villageConverter.toProto(it.village, user) }
                    )
                    .setConquerLevelInfo(getConquerLevelInfo(registeredPlaceAccessibilityCount, registeredBuildingAccessibilityCount))
                    .apply {
                        conquerRankingService.getRanking(userId)?.let {
                            conquerRank = Common.Int32Value.newBuilder().setValue(it).build()
                        }
                    }
                    .setPlaceAccessibilityCount(registeredPlaceAccessibilityCount)
                    // TODO: 읍면동별 숫자 필요
                    .build()
            }
        )
    }
}

fun getConquerLevelInfo(
    registeredPlaceAccessibilityCount: Int,
    registeredBuildingAccessibilityCount: Int
): GetMyPageViewDataResult.ConquerLevelInfo {
    val (level, description) = when {
        registeredPlaceAccessibilityCount + registeredBuildingAccessibilityCount >= 20 -> Pair("Max", "전설의 정복자")
        registeredPlaceAccessibilityCount >= 7 -> Pair("4", "능숙한 정복자")
        registeredPlaceAccessibilityCount >= 3 -> Pair("3", "어엿한 정복자")
        registeredPlaceAccessibilityCount >= 1 -> Pair("2", "새내기 정복자")
        else -> Pair("1", "예비 정복자")
    }
    return GetMyPageViewDataResult.ConquerLevelInfo.newBuilder()
        .setLevel(level)
        .setDescription(description)
        .build()
}
