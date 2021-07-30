package route

import application.placeAccessibility.BuildingAccessibilityUpvoteApplicationService
import auth.UserAuthenticator
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import org.koin.core.context.GlobalContext
import ourMap.protocol.GiveBuildingAccessibilityUpvoteParams
import ourMap.protocol.GiveBuildingAccessibilityUpvoteResult

fun Route.giveBuildingAccessibilityUpvote() {
    val koin = GlobalContext.get()

    val userAuthenticator = koin.get<UserAuthenticator>()
    val buildingAccessibilityUpvoteApplicationService = koin.get<BuildingAccessibilityUpvoteApplicationService>()

    post("/giveBuildingAccessibilityUpvote") {
        val userId = userAuthenticator.checkAuth(call.request)

        val params = call.receive<GiveBuildingAccessibilityUpvoteParams>()
        buildingAccessibilityUpvoteApplicationService.giveUpvote(userId, params.buildingAccessibilityId)

        call.respond(GiveBuildingAccessibilityUpvoteResult.getDefaultInstance())
    }
}
