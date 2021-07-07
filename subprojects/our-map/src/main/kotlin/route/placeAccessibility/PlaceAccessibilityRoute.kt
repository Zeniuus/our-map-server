package route.placeAccessibility

import application.placeAccessibility.PlaceAccessibilityApplicationService
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
import route.converter.BuildingAccessibilityConverter
import route.converter.PlaceAccessibilityConverter

fun Route.placeAccessibilityRoute() {
    val koin = GlobalContext.getKoinApplicationOrNull()!!.koin
    val placeAccessibilityApplicationService = koin.get<PlaceAccessibilityApplicationService>()

    post("/registerAccessibility") {
        // TODO: 인증
        val params = call.receive<RegisterAccessibilityParams>()
        val (placeAccessibility, buildingAccessibility) = placeAccessibilityApplicationService.register(
            createPlaceAccessibilityParams = PlaceAccessibilityService.CreateParams(
                placeId = params.placeAccessibilityParams.placeId,
                isFirstFloor = params.placeAccessibilityParams.isFirstFloor,
                hasStair = params.placeAccessibilityParams.hasStair,
                isWheelchairAccessible = params.placeAccessibilityParams.isWheelchairAccessible,
                userId = null, // TODO
            ),
            createBuildingAccessibilityParams = if (params.hasBuildingAccessibilityParams()) {
                BuildingAccessibilityService.CreateParams(
                    buildingId = params.buildingAccessibilityParams.buildingId,
                    hasElevator = params.buildingAccessibilityParams.hasElevator,
                    hasObstacleToElevator = params.buildingAccessibilityParams.hasObstacleToElevator,
                    stairInfo = BuildingAccessibilityConverter.fromProto(params.buildingAccessibilityParams.stairInfo),
                    userId = null, // TODO
                )
            } else {
                null
            }
        )

        call.respond(
            RegisterAccessibilityResult.newBuilder()
                .setPlaceAccessibility(PlaceAccessibilityConverter.toProto(placeAccessibility))
                .also {
                    if (buildingAccessibility != null) {
                        BuildingAccessibilityConverter.toProto(buildingAccessibility)
                    }
                }
                .build()
        )
    }
}
