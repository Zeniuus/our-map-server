package route

import application.TransactionManager
import application.village.VillageApplicationService
import auth.UserAuthenticator
import converter.VillageConverter
import domain.user.repository.UserRepository
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import org.koin.core.context.GlobalContext
import ourMap.protocol.GetHomeViewDataResult

fun Route.getHomeViewData() {
    val koin = GlobalContext.get()

    val transactionManager = koin.get<TransactionManager>()
    val userAuthenticator = koin.get<UserAuthenticator>()
    val userRepository = koin.get<UserRepository>()
    val villageApplicationService = koin.get<VillageApplicationService>()
    val villageConverter = koin.get<VillageConverter>()

    post("/getHomeViewData") {
        val userId = userAuthenticator.checkAuth(call.request)

        val villages = villageApplicationService.listForMainView()

        call.respond(
            transactionManager.doInTransaction {
                val user = userRepository.findById(userId)
                GetHomeViewDataResult.newBuilder()
                    .addAllEntries(
                        villages.mapIndexed { idx, village ->
                            villageConverter.toRankingEntryProto(village, user, progressRank = idx + 1)
                        }
                    )
                    .build()
            }
        )
    }
}
