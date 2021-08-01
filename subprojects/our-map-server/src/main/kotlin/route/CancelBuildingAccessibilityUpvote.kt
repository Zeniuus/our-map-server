package route

import application.accessibility.BuildingAccessibilityUpvoteApplicationService
import auth.UserAuthenticator
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import org.koin.core.context.GlobalContext
import ourMap.protocol.CancelBuildingAccessibilityUpvoteParams
import ourMap.protocol.CancelBuildingAccessibilityUpvoteResult

fun Route.cancelBuildingAccessibilityUpvote() {
    val koin = GlobalContext.get()

    val userAuthenticator = koin.get<UserAuthenticator>()
    val buildingAccessibilityUpvoteApplicationService = koin.get<BuildingAccessibilityUpvoteApplicationService>()

    post("/cancelBuildingAccessibilityUpvote") {
        val userId = userAuthenticator.checkAuth(call.request)

        val params = call.receive<CancelBuildingAccessibilityUpvoteParams>()
        buildingAccessibilityUpvoteApplicationService.cancelUpvote(userId, params.buildingAccessibilityId)

        call.respond(CancelBuildingAccessibilityUpvoteResult.getDefaultInstance())
    }
}
