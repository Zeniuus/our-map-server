package auth

import application.user.UserAuthApplicationService
import com.google.common.net.HttpHeaders
import domain.user.exception.UserAuthenticationException
import io.ktor.request.ApplicationRequest
import io.ktor.request.header
import io.ktor.response.ApplicationResponse
import io.ktor.response.header

class UserAuthenticator(
    private val userAuthApplicationService: UserAuthApplicationService,
) {
    companion object {
        const val accessTokenHeader = "X-OURMAP-ACCESS-KEY"
    }

    fun setAuth(request: ApplicationResponse, accessToken: String) {
        request.header(accessTokenHeader, accessToken)
    }

    // User ID를 반환한다.
    fun checkAuth(request: ApplicationRequest): String {
        val accessToken = request.header(HttpHeaders.AUTHORIZATION)?.replace("JWT ", "") ?: throw UserAuthenticationException()
        return userAuthApplicationService.verify(accessToken)
    }
}
