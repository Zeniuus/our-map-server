package route.user

import application.user.UserApplicationService
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import org.koin.core.context.GlobalContext
import ourMap.protocol.LoginParams
import ourMap.protocol.LoginResult
import ourMap.protocol.SignUpParams
import ourMap.protocol.SignUpResult
import route.UserAuthenticator

fun Route.userRoutes() {
    val koin = GlobalContext.getKoinApplicationOrNull()!!.koin
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
        call.respond(SignUpResult.getDefaultInstance())
    }

    post("/login") {
        val params = call.receive<LoginParams>()
        val loginResult = userApplicationService.login(
            nickname = params.nickname,
            password = params.password
        )

        userAuthenticator.setAuth(call.response, loginResult.accessToken)
        call.respond(LoginResult.getDefaultInstance())
    }
}
