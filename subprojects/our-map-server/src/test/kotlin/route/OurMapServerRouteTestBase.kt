package route

import ClockMock
import ProtobufJsonConverter
import TestDataGenerator
import application.TransactionManager
import application.village.VillageApplicationService
import com.google.protobuf.Message
import com.google.protobuf.MessageOrBuilder
import configOurMapServerIoCContainerOnce
import domain.user.entity.User
import io.ktor.application.Application
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.isSuccess
import io.ktor.server.testing.TestApplicationCall
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import org.koin.core.context.GlobalContext
import org.koin.dsl.binds
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import ourMap.protocol.Model
import ourMapModule
import java.time.Clock
import kotlin.reflect.KClass

open class OurMapServerRouteTestBase : KoinTest {
    /**
     * [runRouteTest]에서 [ourMapModule]을 실행할 때 Global KoinApplication이 설정되므로
     * 별도로 KoinTestRule을 설정하지 않아도 inject가 가능하다.
     */
    protected val transactionManager by inject<TransactionManager>()
    protected val testDataGenerator = TestDataGenerator()

    private val villageApplicationService by inject<VillageApplicationService>()

    init {
        MySQLContainer.startOnce()
        configOurMapServerIoCContainerOnce()
        // FIXME: 이미 위에서 ClockMock이 아니라 SystemClock을 활용해서 bean을 생성해서,
        //        이 아래서 clock bean을 갈아끼워도 아무 의미가 없다.
        GlobalContext.unloadKoinModules(module {
            single { Clock.systemUTC() }
        })
        GlobalContext.loadKoinModules(module {
            single { ClockMock() }.binds(arrayOf(Clock::class, ClockMock::class))
        })

        villageApplicationService.upsertAll()
    }

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
        assert(response.status()?.isSuccess() == true)
        val builder = kClass.javaObjectType.getMethod("newBuilder").invoke(null) as Message.Builder
        ProtobufJsonConverter.deserializer.merge(response.content!!, builder)
        return builder.build() as T
    }

    @Suppress("UNCHECKED_CAST")
    fun TestApplicationCall.getError(): Model.OurMapError {
        assert(response.status()?.isSuccess() == false)
        val builder = Model.OurMapError.newBuilder()
        ProtobufJsonConverter.deserializer.merge(response.content!!, builder)
        return builder.build()
    }

    fun runRouteTest(block: TestApplicationEngine.() -> Any) {
        withTestApplication(Application::ourMapModule, block)
    }
}
