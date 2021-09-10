import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.jackson.jackson
import io.ktor.routing.routing
import org.koin.core.error.KoinAppAlreadyStartedException
import route.health
import route.runSql

// TODO: embeddedServer로 서버를 띄우려고 하면 install(ContentNegotiation) { ... } 이 두 번 불려서
//       DuplicateApplicationFeatureException가 발생한다. 실험 & 원인 파악 후 에러 리포팅하면 좋을 듯?
//       일단은 EngineMain 방식을 사용하여 문제를 우회한다.
fun main(args: Array<String>) {
    configOurMapServerAdminIoCContainerOnce()
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.ourMapServerAdminModule(testing: Boolean = false) {
    install(ContentNegotiation) {
        jackson()
    }

    install(CORS) {
        HttpMethod.DefaultMethods.forEach {
            method(it)
        }
        header(HttpHeaders.Authorization)
        header(HttpHeaders.Referrer)
        header(HttpHeaders.UserAgent)
        allowNonSimpleContentTypes = true
        allowCredentials = true
        anyHost()
    }

    routing {
        health()
        runSql()
    }
}

fun configOurMapServerAdminIoCContainerOnce() {
    try {
        OurMapIoCFactory.configGlobally { this }
    } catch (e: KoinAppAlreadyStartedException) {
    }
}