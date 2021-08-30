package route

import application.TransactionManager
import application.accessibility.AccessibilityApplicationService
import auth.UserAuthenticator
import converter.BuildingAccessibilityConverter
import converter.PlaceAccessibilityConverter
import domain.accessibility.repository.PlaceAccessibilityRepository
import domain.accessibility.service.BuildingAccessibilityService
import domain.accessibility.service.PlaceAccessibilityService
import domain.user.repository.UserRepository
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
    val userAuthenticator = koin.get<UserAuthenticator>()
    val userRepository = koin.get<UserRepository>()
    val placeAccessibilityRepository = koin.get<PlaceAccessibilityRepository>()
    val placeAccessibilityApplicationService = koin.get<AccessibilityApplicationService>()
    val placeAccessibilityConverter = koin.get<PlaceAccessibilityConverter>()
    val buildingAccessibilityConverter = koin.get<BuildingAccessibilityConverter>()

    post("/registerAccessibility") {
        val userId = userAuthenticator.getUserId(call.request)

        val params = call.receive<RegisterAccessibilityParams>()
        val (placeAccessibility, buildingAccessibility) = placeAccessibilityApplicationService.register(
            createPlaceAccessibilityParams = PlaceAccessibilityService.CreateParams(
                placeId = params.placeAccessibilityParams.placeId,
                isFirstFloor = params.placeAccessibilityParams.isFirstFloor,
                hasStair = params.placeAccessibilityParams.hasStair,
                hasSlope = params.placeAccessibilityParams.hasSlope,
                userId = userId,
            ),
            createBuildingAccessibilityParams = if (params.hasBuildingAccessibilityParams()) {
                BuildingAccessibilityService.CreateParams(
                    buildingId = params.buildingAccessibilityParams.buildingId,
                    entranceStairInfo = BuildingAccessibilityConverter.fromProto(params.buildingAccessibilityParams.entranceStairInfo),
                    hasSlope = params.buildingAccessibilityParams.hasSlope,
                    hasElevator = params.buildingAccessibilityParams.hasElevator,
                    elevatorStairInfo = BuildingAccessibilityConverter.fromProto(params.buildingAccessibilityParams.elevatorStairInfo),
                    userId = userId,
                )
            } else {
                null
            }
        )

        call.respond(
            transactionManager.doInTransaction {
                val user = userId?.let { userRepository.findById(it) }
                RegisterAccessibilityResult.newBuilder()
                    .setPlaceAccessibility(placeAccessibilityConverter.toProto(placeAccessibility))
                    .also {
                        if (buildingAccessibility != null) {
                            it.buildingAccessibility = buildingAccessibilityConverter.toProto(buildingAccessibility, user)
                        }
                    }
                    .setRegisteredUserOrder(placeAccessibilityRepository.countAll())
                    .build()
            }
        )
    }
}
