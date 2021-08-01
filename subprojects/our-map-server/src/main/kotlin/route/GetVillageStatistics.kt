package route

import application.TransactionManager
import application.village.VillageApplicationService
import auth.UserAuthenticator
import converter.UserConverter
import converter.VillageConverter
import domain.user.repository.UserRepository
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import org.koin.core.context.GlobalContext
import ourMap.protocol.GetVillageStatisticsParams
import ourMap.protocol.GetVillageStatisticsResult

fun Route.getVillageStatistics() {
    val koin = GlobalContext.get()

    val transactionManager = koin.get<TransactionManager>()
    val userAuthenticator = koin.get<UserAuthenticator>()
    val userRepository = koin.get<UserRepository>()
    val villageApplicationService = koin.get<VillageApplicationService>()
    val villageConverter = koin.get<VillageConverter>()

    post("/getVillageStatistics") {
        val userId = userAuthenticator.checkAuth(call.request)
        val params = call.receive<GetVillageStatisticsParams>()

        val stats = villageApplicationService.getStatistics(userId, params.villageId)

        call.respond(
            transactionManager.doInTransaction {
                val user = userRepository.findById(userId)
                GetVillageStatisticsResult.newBuilder()
                    .setVillageRankingEntry(villageConverter.toRankingEntryProto(stats.village, user))
                    .setBuildingAccessibilityCount(stats.village.buildingAccessibilityCount)
                    .setTotalBuildingCount(stats.village.buildingCount)
                    .setPlaceAccessibilityCount(stats.village.placeAccessibilityCount)
                    .setTotalPlaceCount(stats.village.placeCount)
                    .setRegisteredUserCount(stats.village.buildingAccessibilityRegisteredUserCount)
                    .setEupMyeonDongName(stats.eupMyeonDong.name)
                    .apply {
                        stats.mostRegisteredUser?.let { mostRegisteredUser = UserConverter.toProto(it) }
                    }
                    .setNextColoringRemainingCount(0) // TODO
                    .build()
            }
        )
    }
}
