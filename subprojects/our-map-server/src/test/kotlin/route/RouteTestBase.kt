package route

import TestDataGenerator
import application.TransactionManager
import com.google.protobuf.Message
import com.google.protobuf.MessageOrBuilder
import domain.user.entity.User
import io.ktor.application.Application
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.server.testing.TestApplicationCall
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import org.koin.test.KoinTest
import org.koin.test.inject
import ourMapModule
import kotlin.reflect.KClass

open class RouteTestBase : KoinTest {
    /**
     * [runRouteTest]에서 [ourMapModule]을 실행할 때 Global KoinApplication이 설정되므로
     * 별도로 KoinTestRule을 설정하지 않아도 inject가 가능하다.
     */
    protected val transactionManager by inject<TransactionManager>()
    protected val testDataGenerator = TestDataGenerator()

    fun TestApplicationEngine.requestWithoutAuth(url: String, params: MessageOrBuilder): TestApplicationCall {
        return handleRequest(HttpMethod.Post, url) {
            addHeader("Content-Type", ContentType.Application.Json.toString())
            setBody(ProtobufJsonConverter.serializer.print(params))
        }
    }

    fun TestApplicationEngine.getTestClient(user: User): RouteTestClient {
        return RouteTestClient(this, user)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : MessageOrBuilder> TestApplicationCall.getResult(kClass: KClass<T>): T {
        val builder = kClass.javaObjectType.getMethod("newBuilder").invoke(null) as Message.Builder
        ProtobufJsonConverter.deserializer.merge(response.content!!, builder)
        return builder.build() as T
    }

    fun runRouteTest(block: TestApplicationEngine.() -> Any) {
        withTestApplication(Application::ourMapModule, block)
    }
}
