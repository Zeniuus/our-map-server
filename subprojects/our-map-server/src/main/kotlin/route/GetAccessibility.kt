package route

import application.TransactionManager
import application.accessibility.AccessibilityApplicationService
import auth.UserAuthenticator
import converter.BuildingAccessibilityConverter
import converter.PlaceAccessibilityConverter
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
    val placeAccessibilityApplicationService = koin.get<AccessibilityApplicationService>()
    val placeAccessibilityConverter = koin.get<PlaceAccessibilityConverter>()
    val buildingAccessibilityConverter = koin.get<BuildingAccessibilityConverter>()

    post("/getAccessibility") {
        val userId = userAuthenticator.checkAuth(call.request)

        val params = call.receive<GetAccessibilityParams>()
        val (placeAccessibility, buildingAccessibility) = placeAccessibilityApplicationService.getAccessibility(params.placeId)

        call.respond(
            transactionManager.doInTransaction {
                val user = userRepository.findById(userId)
                GetAccessibilityResult.newBuilder()
                    .also {
                        if (placeAccessibility != null) {
                            it.placeAccessibility = placeAccessibilityConverter.toProto(placeAccessibility)
                        }
                    }
                    .also {
                        if (buildingAccessibility != null) {
                            it.buildingAccessibility = buildingAccessibilityConverter.toProto(buildingAccessibility, user)
                        }
                    }
                    .build()
            }
        )
    }
}
