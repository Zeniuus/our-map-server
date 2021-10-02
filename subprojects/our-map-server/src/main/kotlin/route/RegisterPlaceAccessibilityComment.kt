package route

import application.TransactionManager
import application.accessibility.AccessibilityApplicationService
import auth.UserAuthenticator
import converter.PlaceAccessibilityCommentConverter
import domain.accessibility.repository.PlaceAccessibilityCommentRepository
import domain.accessibility.service.PlaceAccessibilityCommentService
import domain.user.repository.UserRepository
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import org.koin.core.context.GlobalContext
import ourMap.protocol.RegisterPlaceAccessibilityCommentParams
import ourMap.protocol.RegisterPlaceAccessibilityCommentResult

fun Route.registerPlaceAccessibilityComment() {
    val koin = GlobalContext.get()
    val transactionManager = koin.get<TransactionManager>()
    val userAuthenticator = koin.get<UserAuthenticator>()
    val userRepository = koin.get<UserRepository>()
    val placeAccessibilityCommentRepository = koin.get<PlaceAccessibilityCommentRepository>()
    val accessibilityApplicationService = koin.get<AccessibilityApplicationService>()

    post("/registerPlaceAccessibilityComment") {
        val userId = userAuthenticator.getUserId(call.request)

        val params = call.receive<RegisterPlaceAccessibilityCommentParams>()
        accessibilityApplicationService.registerPlaceAccessibilityComment(
            params = PlaceAccessibilityCommentService.CreateParams(
                placeId = params.placeId,
                userId = userId,
                comment = params.comment,
            ),
        )

        call.respond(
            transactionManager.doInTransaction {
                val comments = placeAccessibilityCommentRepository.findByPlaceId(params.placeId)
                val userCache = userRepository.findByIdIn(comments.mapNotNull { it.userId })
                    .associateBy { it.id }
                RegisterPlaceAccessibilityCommentResult.newBuilder()
                    .addAllComments(comments.map { PlaceAccessibilityCommentConverter.toProto(it, userCache) })
                    .build()
            }
        )
    }
}
