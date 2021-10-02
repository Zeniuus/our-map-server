package route

import application.TransactionManager
import application.accessibility.AccessibilityApplicationService
import auth.UserAuthenticator
import converter.BuildingAccessibilityCommentConverter
import domain.accessibility.repository.BuildingAccessibilityCommentRepository
import domain.accessibility.service.BuildingAccessibilityCommentService
import domain.user.repository.UserRepository
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import org.koin.core.context.GlobalContext
import ourMap.protocol.RegisterBuildingAccessibilityCommentParams
import ourMap.protocol.RegisterBuildingAccessibilityCommentResult

fun Route.registerBuildingAccessibilityComment() {
    val koin = GlobalContext.get()
    val transactionManager = koin.get<TransactionManager>()
    val userAuthenticator = koin.get<UserAuthenticator>()
    val userRepository = koin.get<UserRepository>()
    val buildingAccessibilityCommentRepository = koin.get<BuildingAccessibilityCommentRepository>()
    val accessibilityApplicationService = koin.get<AccessibilityApplicationService>()

    post("/registerBuildingAccessibilityComment") {
        val userId = userAuthenticator.getUserId(call.request)

        val params = call.receive<RegisterBuildingAccessibilityCommentParams>()
        accessibilityApplicationService.registerBuildingAccessibilityComment(
            params = BuildingAccessibilityCommentService.CreateParams(
                buildingId = params.buildingId,
                userId = userId,
                comment = params.comment,
            ),
        )

        call.respond(
            transactionManager.doInTransaction {
                val comments = buildingAccessibilityCommentRepository.findByBuildingId(params.buildingId)
                val userCache = userRepository.findByIdIn(comments.mapNotNull { it.userId })
                    .associateBy { it.id }
                RegisterBuildingAccessibilityCommentResult.newBuilder()
                    .addAllComments(comments.map { BuildingAccessibilityCommentConverter.toProto(it, userCache) })
                    .build()
            }
        )
    }
}
