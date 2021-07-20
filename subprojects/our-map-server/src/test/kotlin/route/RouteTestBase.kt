package route

import com.google.protobuf.Message
import com.google.protobuf.MessageOrBuilder
import io.ktor.application.Application
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.server.testing.TestApplicationCall
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import ourMapModule
import kotlin.reflect.KClass

open class RouteTestBase {
    fun TestApplicationEngine.request(url: String, params: MessageOrBuilder): TestApplicationCall {
        return handleRequest(HttpMethod.Post, url) {
            addHeader("Content-Type", ContentType.Application.Json.toString())
            setBody(ProtobufJsonConverter.serializer.print(params))
        }
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
