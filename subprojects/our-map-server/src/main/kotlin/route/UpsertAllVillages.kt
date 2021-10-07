package route

import application.village.VillageApplicationService
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import org.koin.core.context.GlobalContext

fun Route.upsertAllVillages() {
    val koin = GlobalContext.get()

    val villageApplicationService = koin.get<VillageApplicationService>()

    post("/upsertAllVillages") {
        villageApplicationService.upsertAll()
        call.respond("ok")
    }
}
