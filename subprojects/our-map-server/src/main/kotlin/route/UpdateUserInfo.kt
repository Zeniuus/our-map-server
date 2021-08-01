package route

import application.TransactionManager
import application.user.UserApplicationService
import auth.UserAuthenticator
import converter.UserConverter
import domain.user.repository.UserRepository
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import org.koin.core.context.GlobalContext
import ourMap.protocol.UpdateUserInfoParams
import ourMap.protocol.UpdateUserInfoResult

fun Route.updateUserInfo() {
    val koin = GlobalContext.get()

    val transactionManager = koin.get<TransactionManager>()
    val userAuthenticator = koin.get<UserAuthenticator>()
    val userRepository = koin.get<UserRepository>()
    val userApplicationService = koin.get<UserApplicationService>()

    post("/updateUserInfo") {
        val userId = userAuthenticator.checkAuth(call.request)
        val params = call.receive<UpdateUserInfoParams>()

        userApplicationService.updateUserInfo(
            userId = userId,
            nickname = params.nickname,
            instagramId = if (params.hasInstagramId()) {
                params.instagramId.value
            } else {
                null
            },
        )

        call.respond(
            transactionManager.doInTransaction {
                val user = userRepository.findById(userId)
                UpdateUserInfoResult.newBuilder()
                    .setUser(UserConverter.toProto(user))
                    .build()
            }
        )
    }
}
