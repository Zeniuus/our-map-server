package route

import application.TransactionManager
import com.google.protobuf.MessageOrBuilder
import domain.user.entity.User
import domain.user.service.UserAuthService
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.server.testing.TestApplicationCall
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import org.koin.core.context.GlobalContext

class RouteTestClient(
    private val testApplicationEngine: TestApplicationEngine,
    private val user: User,
) {
    private val userAuthService = GlobalContext.getKoinApplicationOrNull()!!.koin.get<UserAuthService>()
    private val transactionManager = GlobalContext.getKoinApplicationOrNull()!!.koin.get<TransactionManager>()

    private val accessToken = transactionManager.doInTransaction {
        userAuthService.issueAccessToken(user)
    }

    fun request(url: String, params: MessageOrBuilder): TestApplicationCall {
        return testApplicationEngine.handleRequest(HttpMethod.Post, url) {
            addHeader(UserAuthenticator.accessTokenHeader, accessToken)
            addHeader("Content-Type", ContentType.Application.Json.toString())
            setBody(ProtobufJsonConverter.serializer.print(params))
        }
    }
}