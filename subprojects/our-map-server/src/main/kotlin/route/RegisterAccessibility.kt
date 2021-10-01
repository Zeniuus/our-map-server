package route

import application.TransactionManager
import application.accessibility.AccessibilityApplicationService
import auth.UserAuthenticator
import converter.BuildingAccessibilityCommentConverter
import converter.BuildingAccessibilityConverter
import converter.PlaceAccessibilityCommentConverter
import converter.PlaceAccessibilityConverter
import converter.StairInfoConverter
import domain.accessibility.repository.PlaceAccessibilityRepository
import domain.accessibility.service.BuildingAccessibilityCommentService
import domain.accessibility.service.BuildingAccessibilityService
import domain.accessibility.service.PlaceAccessibilityCommentService
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
        val result = placeAccessibilityApplicationService.register(
            createPlaceAccessibilityParams = PlaceAccessibilityService.CreateParams(
                placeId = params.placeAccessibilityParams.placeId,
                isFirstFloor = params.placeAccessibilityParams.isFirstFloor,
                stairInfo = StairInfoConverter.fromProto(params.placeAccessibilityParams.stairInfo),
                hasSlope = params.placeAccessibilityParams.hasSlope,
                userId = userId,
            ),
            createPlaceAccessibilityCommentParams = if (params.placeAccessibilityParams.hasComment()) {
                PlaceAccessibilityCommentService.CreateParams(
                    placeId = params.placeAccessibilityParams.placeId,
                    userId = userId,
                    comment = params.placeAccessibilityParams.comment.value,
                )
            } else {
                null
            },
            createBuildingAccessibilityParams = if (params.hasBuildingAccessibilityParams()) {
                BuildingAccessibilityService.CreateParams(
                    buildingId = params.buildingAccessibilityParams.buildingId,
                    entranceStairInfo = StairInfoConverter.fromProto(params.buildingAccessibilityParams.entranceStairInfo),
                    hasSlope = params.buildingAccessibilityParams.hasSlope,
                    hasElevator = params.buildingAccessibilityParams.hasElevator,
                    elevatorStairInfo = StairInfoConverter.fromProto(params.buildingAccessibilityParams.elevatorStairInfo),
                    userId = userId,
                )
            } else {
                null
            },
            createBuildingAccessibilityCommentParams = if (params.buildingAccessibilityParams.hasComment()) {
                BuildingAccessibilityCommentService.CreateParams(
                    buildingId = params.buildingAccessibilityParams.buildingId,
                    userId = userId,
                    comment = params.buildingAccessibilityParams.comment.value,
                )
            } else {
                null
            },
        )

        call.respond(
            transactionManager.doInTransaction {
                val user = userId?.let { userRepository.findById(it) }
                val userCache = user?.let { mapOf(it.id to it) } ?: emptyMap()
                RegisterAccessibilityResult.newBuilder()
                    .setPlaceAccessibility(placeAccessibilityConverter.toProto(result.placeAccessibility))
                    .also {
                        if (result.placeAccessibilityComment != null) {
                            it.addPlaceAccessibilityComments(PlaceAccessibilityCommentConverter.toProto(result.placeAccessibilityComment!!, userCache))
                        }
                    }
                    .also {
                        if (result.buildingAccessibility != null) {
                            it.buildingAccessibility = buildingAccessibilityConverter.toProto(result.buildingAccessibility!!, user)
                        }
                    }
                    .also {
                        if (result.buildingAccessibilityComment != null) {
                            it.addBuildingAccessibilityComments(BuildingAccessibilityCommentConverter.toProto(result.buildingAccessibilityComment!!, userCache))
                        }
                    }
                    .setRegisteredUserOrder(placeAccessibilityRepository.countAll())
                    .build()
            }
        )
    }
}
