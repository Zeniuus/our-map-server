package route.user

import application.user.UserApplicationService
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.header
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import org.koin.core.context.GlobalContext
import ourmap.protocol.SignUpParams
import ourmap.protocol.SignUpResult

fun Route.userRoutes() {
    val koin = GlobalContext.getKoinApplicationOrNull()!!.koin
    val userApplicationService = koin.get<UserApplicationService>()

    post("/signUp") {
        val params = call.receive<SignUpParams>()
        val user = userApplicationService.signUp(
            nickname = params.nickname,
            email = params.email,
            password = params.password,
            instagramId = if (params.hasInstagramId()) {
                params.instagramId.value
            } else {
                null
            }
        )

        SignUpResult.newBuilder()
        call.response.header("X-OURMAP-ACCESS-KEY", "access-key") // TODO: real access key
        call.respond(SignUpResult.getDefaultInstance())
    }
}
