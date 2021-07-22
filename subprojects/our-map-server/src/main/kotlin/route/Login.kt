package route

import application.user.UserApplicationService
import auth.UserAuthenticator
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import org.koin.core.context.GlobalContext
import ourMap.protocol.LoginParams
import ourMap.protocol.LoginResult

fun Route.login() {
    val koin = GlobalContext.getKoinApplicationOrNull()!!.koin
    val userApplicationService = koin.get<UserApplicationService>()
    val userAuthenticator = koin.get<UserAuthenticator>()

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
