import application.user.userApplicationModule
import domain.user.userDomainModule
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.routing.routing
import org.koin.core.context.startKoin
import route.ProtobufJsonContentConverter
import route.user.userRoutes

// TODO: embeddedServer로 서버를 띄우려고 하면 install(ContentNegotiation) { ... } 이 두 번 불려서
//       DuplicateApplicationFeatureException가 발생한다. 실험 & 원인 파악 후 에러 리포팅하면 좋을 듯?
//       일단은 EngineMain 방식을 사용하여 문제를 우회한다.
fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.ourMapModule(testing: Boolean = false) {
    startKoin {
        modules(
            userDomainModule,
            userApplicationModule,
        )
    }

    install(ContentNegotiation) {
        register(ContentType.Application.Json, ProtobufJsonContentConverter())
    }

    routing {
        userRoutes()
    }
}
