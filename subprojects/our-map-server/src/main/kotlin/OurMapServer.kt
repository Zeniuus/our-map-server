import application.accessibility.accessibilityApplicationModule
import application.place.placeApplicationModule
import application.user.userApplicationModule
import application.village.villageApplicationModule
import auth.UserAuthenticator
import auth.ourMapAuthModule
import converter.ourMapConverterModule
import domain.accessibility.accessibilityDomainModule
import domain.place.placeDomainModule
import domain.user.userDomainModule
import domain.village.villageDomainModule
import exception.OurMapExceptionHandler
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.response.respond
import io.ktor.routing.routing
import org.koin.core.error.KoinAppAlreadyStartedException
import route.cancelBuildingAccessibilityUpvote
import route.getAccessibility
import route.getHomeViewData
import route.getMyPageViewData
import route.getVillageStatistics
import route.giveBuildingAccessibilityUpvote
import route.listAdministrativeAreas
import route.listVillageDropdownItems
import route.login
import route.registerAccessibility
import route.registerFavoriteVillage
import route.searchPlaces
import route.signUp
import route.unregisterFavoriteVillage
import route.updateUserInfo

// TODO: embeddedServer로 서버를 띄우려고 하면 install(ContentNegotiation) { ... } 이 두 번 불려서
//       DuplicateApplicationFeatureException가 발생한다. 실험 & 원인 파악 후 에러 리포팅하면 좋을 듯?
//       일단은 EngineMain 방식을 사용하여 문제를 우회한다.
fun main(args: Array<String>) {
    LiquibaseDatabaseMigrator.migrate()
    configOurMapServerIoCContainerOnce()
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.ourMapModule(testing: Boolean = false) {
    install(ContentNegotiation) {
        register(ContentType.Application.Json, ProtobufJsonContentConverter())
    }

    install(CORS) {
        HttpMethod.DefaultMethods.forEach {
            method(it)
        }
        header(HttpHeaders.Authorization)
        header(HttpHeaders.Referrer)
        header(HttpHeaders.UserAgent)
        exposeHeader(UserAuthenticator.accessTokenHeader)
        allowNonSimpleContentTypes = true
        allowCredentials = true
        anyHost()
    }

    install(StatusPages) {
        exception<Throwable> {
            val result = OurMapExceptionHandler.handle(it)
            call.respond(result.statusCode, result.body)
        }
    }

    routing {
        signUp()
        login()
        getHomeViewData()
        listAdministrativeAreas()
        searchPlaces()
        getAccessibility()
        giveBuildingAccessibilityUpvote()
        cancelBuildingAccessibilityUpvote()
        registerAccessibility()
        listVillageDropdownItems()
        getVillageStatistics()
        registerFavoriteVillage()
        unregisterFavoriteVillage()
        getMyPageViewData()
        updateUserInfo()
    }
}

fun configOurMapServerIoCContainerOnce() {
    try {
        OurMapIoCFactory.configGlobally {
            modules(
                userDomainModule,
                userApplicationModule,

                placeDomainModule,
                placeApplicationModule,

                accessibilityDomainModule,
                accessibilityApplicationModule,

                villageDomainModule,
                villageApplicationModule,

                ourMapConverterModule,
                ourMapAuthModule,
            )
        }
    } catch (e: KoinAppAlreadyStartedException) {
    }
}
