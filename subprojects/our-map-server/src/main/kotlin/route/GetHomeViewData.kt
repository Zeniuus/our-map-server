package route

import application.TransactionManager
import application.village.VillageApplicationService
import auth.UserAuthenticator
import domain.village.repository.EupMyeonDongRepository
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import org.koin.core.context.GlobalContext
import ourMap.protocol.GetHomeViewDataResult
import java.math.BigDecimal

fun Route.getHomeViewData() {
    val koin = GlobalContext.get()

    val transactionManager = koin.get<TransactionManager>()
    val userAuthenticator = koin.get<UserAuthenticator>()
    val eupMyeonDongRepository = koin.get<EupMyeonDongRepository>()
    val villageApplicationService = koin.get<VillageApplicationService>()

    post("/getHomeViewData") {
        userAuthenticator.checkAuth(call.request)

        val villages = villageApplicationService.listForMainView()

        call.respond(
            transactionManager.doInTransaction {
                GetHomeViewDataResult.newBuilder()
                    .addAllVillageRankingEntries(
                        villages.mapIndexed { idx, village ->
                            val eupMyeonDong = eupMyeonDongRepository.findById(village.eupMyeonDongId)
                            GetHomeViewDataResult.VillageRankingEntry.newBuilder()
                                .setVillageId(village.id)
                                .setVillageName("${eupMyeonDong.siGunGu.name} ${eupMyeonDong.name}")
                                .setProgressRank(idx + 1)
                                .setProgressPercentage(
                                    (village.registerProgress * BigDecimal(100)).toString()
                                        // 소수점 부분이 0으로 끝나는 경우 처리
                                        .replace(Regex("0+$"), "")
                                        .replace(Regex("\\.$"), ""))
                                .build()
                        }
                    )
                    .build()
            }
        )
    }
}
