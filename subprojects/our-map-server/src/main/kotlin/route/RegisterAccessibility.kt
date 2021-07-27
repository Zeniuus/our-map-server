package route

import application.TransactionManager
import application.placeAccessibility.PlaceAccessibilityApplicationService
import auth.UserAuthenticator
import converter.BuildingAccessibilityConverter
import converter.PlaceAccessibilityConverter
import domain.placeAccessibility.service.BuildingAccessibilityService
import domain.placeAccessibility.service.PlaceAccessibilityService
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import org.koin.core.context.GlobalContext
import ourMap.protocol.RegisterAccessibilityParams
import ourMap.protocol.RegisterAccessibilityResult

fun Route.registerAccessibility() {
    val koin = GlobalContext.get()
    val transactionManager = koin.get<TransactionManager>()
    val placeAccessibilityApplicationService = koin.get<PlaceAccessibilityApplicationService>()
    val userAuthenticator = koin.get<UserAuthenticator>()
    val placeAccessibilityConverter = koin.get<PlaceAccessibilityConverter>()
    val buildingAccessibilityConverter = koin.get<BuildingAccessibilityConverter>()

    post("/registerAccessibility") {
        val userId = userAuthenticator.checkAuth(call.request)

        val params = call.receive<RegisterAccessibilityParams>()
        val (placeAccessibility, buildingAccessibility) = placeAccessibilityApplicationService.register(
            createPlaceAccessibilityParams = PlaceAccessibilityService.CreateParams(
                placeId = params.placeAccessibilityParams.placeId,
                isFirstFloor = params.placeAccessibilityParams.isFirstFloor,
                hasStair = params.placeAccessibilityParams.hasStair,
                isWheelchairAccessible = params.placeAccessibilityParams.isWheelchairAccessible,
                userId = userId,
            ),
            createBuildingAccessibilityParams = if (params.hasBuildingAccessibilityParams()) {
                BuildingAccessibilityService.CreateParams(
                    buildingId = params.buildingAccessibilityParams.buildingId,
                    hasElevator = params.buildingAccessibilityParams.hasElevator,
                    hasObstacleToElevator = params.buildingAccessibilityParams.hasObstacleToElevator,
                    stairInfo = BuildingAccessibilityConverter.fromProto(params.buildingAccessibilityParams.stairInfo),
                    userId = userId,
                )
            } else {
                null
            }
        )

        call.respond(
            transactionManager.doInTransaction {
                RegisterAccessibilityResult.newBuilder()
                    .setPlaceAccessibility(placeAccessibilityConverter.toProto(placeAccessibility))
                    .also {
                        if (buildingAccessibility != null) {
                            it.buildingAccessibility = buildingAccessibilityConverter.toProto(buildingAccessibility)
                        }
                    }
                    .build()
            }
        )
    }
}
