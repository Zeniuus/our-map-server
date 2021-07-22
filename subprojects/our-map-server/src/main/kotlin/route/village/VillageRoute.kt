package route.village

import application.TransactionManager
import application.village.VillageApplicationService
import domain.village.repository.EupMyeonDongRepository
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import org.koin.core.context.GlobalContext
import ourMap.protocol.GetMainViewDataResult
import route.UserAuthenticator

fun Route.villageRoutes() {
    val koin = GlobalContext.getKoinApplicationOrNull()!!.koin

    val transactionManager = koin.get<TransactionManager>()
    val userAuthenticator = koin.get<UserAuthenticator>()
    val eupMyeonDongRepository = koin.get<EupMyeonDongRepository>()
    val villageApplicationService = koin.get<VillageApplicationService>()

    post("/getMainViewData") {
        userAuthenticator.checkAuth(call.request)

        val villages = villageApplicationService.listForMainView()

        call.respond(
            transactionManager.doInTransaction {
                GetMainViewDataResult.newBuilder()
                    .addAllVillageRankingEntries(
                        villages.mapIndexed { idx, village ->
                            val eupMyeonDong = eupMyeonDongRepository.findById(village.eupMyeonDongId)
                            GetMainViewDataResult.VillageRankingEntry.newBuilder()
                                .setVillageId(village.id)
                                .setVillageName("${eupMyeonDong.siGunGu.name} ${eupMyeonDong.name}")
                                .setRank(idx + 1)
                                .setProgressPercentage(village.registerProgress.toString())
                                .build()
                        }
                    )
                    .build()
            }
        )
    }
}
