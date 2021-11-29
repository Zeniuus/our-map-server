import exception.OurMapServerAdminExceptionHandler
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.routing.routing
import job.OurMapServerAdminJobRunner
import job.ourMapServerAdminJobModule
import org.koin.core.context.GlobalContext
import org.koin.core.error.KoinAppAlreadyStartedException
import org.koin.dsl.module
import quest.application.clubQuestApplicationModule
import quest.domain.clubQuestDomainModule
import route.ClubQuestRouteHandlers
import route.downloadSqlResultAsTsv
import route.health
import route.runSql
import util.utilModule

// TODO: embeddedServer로 서버를 띄우려고 하면 install(ContentNegotiation) { ... } 이 두 번 불려서
//       DuplicateApplicationFeatureException가 발생한다. 실험 & 원인 파악 후 에러 리포팅하면 좋을 듯?
//       일단은 EngineMain 방식을 사용하여 문제를 우회한다.
fun main(args: Array<String>) {
    configOurMapServerAdminIoCContainerOnce()
    io.ktor.server.netty.EngineMain.main(args)

    val jobRunner = GlobalContext.get().get<OurMapServerAdminJobRunner>()
    jobRunner.startJobs()
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
        exposeHeader("Content-Disposition")
    }

    install(StatusPages) {
        exception<Throwable> {
            val result = OurMapServerAdminExceptionHandler.handle(it)
            call.respond(result.statusCode, result.body)
        }
    }

    routing {
        health()
        runSql()
        downloadSqlResultAsTsv()

        // TODO: 더 좋은 패턴으로 정리
        val koin = GlobalContext.get()
        val clubQuestRouteHandlers = koin.get<ClubQuestRouteHandlers>()
        apply(clubQuestRouteHandlers::createClubQuest)
        apply(clubQuestRouteHandlers::deleteClubQuest)
        apply(clubQuestRouteHandlers::getClubQuest)
        apply(clubQuestRouteHandlers::listClubQuests)
        apply(clubQuestRouteHandlers::setPlaceIsCompleted)
        apply(clubQuestRouteHandlers::setPlaceIsClosed)
        apply(clubQuestRouteHandlers::setPlaceIsNotAccessible)
    }
}

fun configOurMapServerAdminIoCContainerOnce() {
    try {
        OurMapIoCFactory.configGlobally {
            modules(utilModule)
            modules(module {
                single { ClubQuestRouteHandlers(get()) }
            })
            modules(ourMapServerAdminJobModule)
            modules(
                clubQuestDomainModule,
                clubQuestApplicationModule,
            )
        }
    } catch (e: KoinAppAlreadyStartedException) {
    }
}
