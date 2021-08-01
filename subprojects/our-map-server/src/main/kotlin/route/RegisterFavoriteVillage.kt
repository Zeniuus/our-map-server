package route

import application.TransactionManager
import application.village.UserFavoriteVillageApplicationService
import auth.UserAuthenticator
import converter.VillageConverter
import domain.user.repository.UserRepository
import domain.village.repository.VillageRepository
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import org.koin.core.context.GlobalContext
import ourMap.protocol.RegisterFavoriteVillageParams
import ourMap.protocol.RegisterFavoriteVillageResult

fun Route.registerFavoriteVillage() {
    val koin = GlobalContext.get()

    val transactionManager = koin.get<TransactionManager>()
    val userAuthenticator = koin.get<UserAuthenticator>()
    val userRepository = koin.get<UserRepository>()
    val villageRepository = koin.get<VillageRepository>()
    val villageConverter = koin.get<VillageConverter>()
    val userFavoriteVillageApplicationService = koin.get<UserFavoriteVillageApplicationService>()

    post("/registerFavoriteVillage") {
        val userId = userAuthenticator.checkAuth(call.request)
        val params = call.receive<RegisterFavoriteVillageParams>()

        userFavoriteVillageApplicationService.register(userId, params.villageId)

        call.respond(
            transactionManager.doInTransaction {
                val user = userRepository.findById(userId)
                val village = villageRepository.findById(params.villageId)
                RegisterFavoriteVillageResult.newBuilder()
                    .setVillage(villageConverter.toProto(village, user))
                    .build()
            }
        )
    }
}
