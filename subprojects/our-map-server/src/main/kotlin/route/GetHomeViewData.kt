package route

import application.TransactionManager
import application.village.VillageApplicationService
import auth.UserAuthenticator
import converter.VillageConverter
import domain.village.service.VillageService
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
    val villageService = koin.get<VillageService>()
    val villageApplicationService = koin.get<VillageApplicationService>()

    post("/getHomeViewData") {
        userAuthenticator.checkAuth(call.request)

        val villages = villageApplicationService.listForMainView()

        call.respond(
            transactionManager.doInTransaction {
                GetHomeViewDataResult.newBuilder()
                    .addAllVillageRankingEntries(
                        villages.mapIndexed { idx, village ->
                            GetHomeViewDataResult.VillageRankingEntry.newBuilder()
                                .setVillageId(village.id)
                                .setVillageName(villageService.getName(village))
                                .setProgressRank(idx + 1)
                                .setProgressPercentage(VillageConverter.getProgressPercentage(village))
                                .build()
                        }
                    )
                    .build()
            }
        )
    }
}
