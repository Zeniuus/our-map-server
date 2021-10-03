package route

import application.TransactionManager
import auth.UserAuthenticator
import converter.UserConverter
import converter.VillageConverter
import domain.accessibility.repository.BuildingAccessibilityUpvoteRepository
import domain.user.repository.UserRepository
import domain.village.repository.UserFavoriteVillageRepository
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import org.koin.core.context.GlobalContext
import ourMap.protocol.GetMyPageViewDataResult

fun Route.getMyPageViewData() {
    val koin = GlobalContext.get()

    val transactionManager = koin.get<TransactionManager>()
    val userAuthenticator = koin.get<UserAuthenticator>()
    val userRepository = koin.get<UserRepository>()
    val userFavoriteVillageRepository = koin.get<UserFavoriteVillageRepository>()
    val buildingAccessibilityUpvoteRepository = koin.get<BuildingAccessibilityUpvoteRepository>()
    val villageConverter = koin.get<VillageConverter>()

    post("/getMyPageViewData") {
        val userId = userAuthenticator.checkAuth(call.request)

        call.respond(
            transactionManager.doInTransaction {
                val user = userRepository.findById(userId)
                val favoriteVillages = userFavoriteVillageRepository.findByUserAndNotDeleted(user)
                GetMyPageViewDataResult.newBuilder()
                    .setUser(UserConverter.toProto(user))
                    .addAllFavoriteVillages(
                        favoriteVillages.map { villageConverter.toProto(it.village, user) }
                    )
                    .setTotalUpvoteCount(buildingAccessibilityUpvoteRepository.getTotalUpvoteCount(user))
                    .build()
            }
        )
    }
}
