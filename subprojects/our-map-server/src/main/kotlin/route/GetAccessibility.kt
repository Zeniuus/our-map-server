package route

import application.TransactionManager
import application.accessibility.AccessibilityApplicationService
import auth.UserAuthenticator
import converter.BuildingAccessibilityCommentConverter
import converter.BuildingAccessibilityConverter
import converter.PlaceAccessibilityCommentConverter
import converter.PlaceAccessibilityConverter
import domain.place.repository.PlaceRepository
import domain.user.repository.UserRepository
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import org.koin.core.context.GlobalContext
import ourMap.protocol.GetAccessibilityParams
import ourMap.protocol.GetAccessibilityResult

fun Route.getAccessibility() {
    val koin = GlobalContext.get()
    val transactionManager = koin.get<TransactionManager>()
    val userAuthenticator = koin.get<UserAuthenticator>()
    val userRepository = koin.get<UserRepository>()
    val placeRepository = koin.get<PlaceRepository>()
    val placeAccessibilityApplicationService = koin.get<AccessibilityApplicationService>()
    val placeAccessibilityConverter = koin.get<PlaceAccessibilityConverter>()
    val buildingAccessibilityConverter = koin.get<BuildingAccessibilityConverter>()

    post("/getAccessibility") {
        val userId = userAuthenticator.getUserId(call.request)

        val params = call.receive<GetAccessibilityParams>()
        val result = placeAccessibilityApplicationService.getAccessibility(params.placeId)

        call.respond(
            transactionManager.doInTransaction {
                val user = userId?.let { userRepository.findById(it) }
                val commentedUserIds = (result.buildingAccessibilityComments.mapNotNull { it.userId } + result.placeAccessibilityComments.mapNotNull { it.userId })
                val commentedUserCache = userRepository.findByIdIn(commentedUserIds)
                    .associateBy { it.id }
                GetAccessibilityResult.newBuilder()
                    .also {
                        if (result.placeAccessibility != null) {
                            it.placeAccessibility = placeAccessibilityConverter.toProto(result.placeAccessibility!!)
                        }
                    }
                    .addAllPlaceAccessibilityComments(result.placeAccessibilityComments.map { PlaceAccessibilityCommentConverter.toProto(it, commentedUserCache) })
                    .also {
                        if (result.buildingAccessibility != null) {
                            it.buildingAccessibility = buildingAccessibilityConverter.toProto(result.buildingAccessibility!!, user)
                        }
                    }
                    .addAllBuildingAccessibilityComments(result.buildingAccessibilityComments.map { BuildingAccessibilityCommentConverter.toProto(it, commentedUserCache) })
                    .setHasOtherPlacesToRegisterInBuilding(result.hasOtherPlacesToRegisterInSameBuilding)
                    .build()
            }
        )
    }
}
