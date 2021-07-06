package route.user

import application.user.UserApplicationService
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.header
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import org.koin.core.context.GlobalContext
import ourMap.protocol.LoginParams
import ourMap.protocol.LoginResult
import ourMap.protocol.SignUpParams
import ourMap.protocol.SignUpResult

fun Route.userRoutes() {
    val koin = GlobalContext.getKoinApplicationOrNull()!!.koin
    val userApplicationService = koin.get<UserApplicationService>()

    post("/signUp") {
        val params = call.receive<SignUpParams>()
        val loginResult = userApplicationService.signUp(
            nickname = params.nickname,
            email = params.email,
            password = params.password,
            instagramId = if (params.hasInstagramId()) {
                params.instagramId.value
            } else {
                null
            }
        )

        call.response.header("X-OURMAP-ACCESS-KEY", loginResult.accessToken)
        call.respond(SignUpResult.getDefaultInstance())
    }

    post("/login") {
        val params = call.receive<LoginParams>()
        val loginResult = userApplicationService.login(
            email = params.email,
            password = params.password
        )

        call.response.header("X-OURMAP-ACCESS-KEY", loginResult.accessToken)
        call.respond(LoginResult.getDefaultInstance())
    }
}
