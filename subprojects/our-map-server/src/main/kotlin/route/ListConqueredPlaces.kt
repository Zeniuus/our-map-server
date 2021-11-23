package route

import application.place.PlaceApplicationService
import auth.UserAuthenticator
import converter.SearchPlacesConverter
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import org.koin.core.context.GlobalContext
import ourMap.protocol.ListConqueredPlacesResult

fun Route.listConqueredPlaces() {
    val koin = GlobalContext.get()

    val userAuthenticator = koin.get<UserAuthenticator>()
    val placeApplicationService = koin.get<PlaceApplicationService>()

    post("/listConqueredPlaces") {
        val userId = userAuthenticator.checkAuth(call.request)
        val results = placeApplicationService.listConqueredPlaces(userId)

        call.respond(
            ListConqueredPlacesResult.newBuilder()
                .addAllItems(results.map { SearchPlacesConverter.convertItem(it) })
                .build()
        )
    }
}
