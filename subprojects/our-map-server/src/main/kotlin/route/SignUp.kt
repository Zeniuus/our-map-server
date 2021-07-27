package route

import application.user.UserApplicationService
import auth.UserAuthenticator
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import org.koin.core.context.GlobalContext
import ourMap.protocol.SignUpParams

fun Route.signUp() {
    val koin = GlobalContext.get()
    val userApplicationService = koin.get<UserApplicationService>()
    val userAuthenticator = koin.get<UserAuthenticator>()

    post("/signUp") {
        val params = call.receive<SignUpParams>()
        val loginResult = userApplicationService.signUp(
            nickname = params.nickname,
            password = params.password,
            instagramId = if (params.hasInstagramId()) {
                params.instagramId.value
            } else {
                null
            }
        )

        userAuthenticator.setAuth(call.response, loginResult.accessToken)
        call.respond(ourMap.protocol.SignUpResult.getDefaultInstance())
    }
}
