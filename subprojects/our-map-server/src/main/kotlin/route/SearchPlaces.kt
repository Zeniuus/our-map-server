package route

import application.place.PlaceApplicationService
import converter.SearchPlacesConverter
import domain.util.Length
import domain.util.Location
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import org.koin.core.context.GlobalContext
import ourMap.protocol.SearchPlacesParams
import ourMap.protocol.SearchPlacesResult

fun Route.searchPlaces() {
    val koin = GlobalContext.get()

    val placeApplicationService = koin.get<PlaceApplicationService>()

    post("/searchPlaces") {
        val params = call.receive<SearchPlacesParams>()
        val results = placeApplicationService.searchPlaces(
            searchText = params.searchText,
            location = if (params.hasCurrentLocation()) {
                Location(
                    lng = params.currentLocation.lng,
                    lat = params.currentLocation.lat,
                )
            } else {
                null
            },
            maxDistance = params.distanceMetersLimit.takeIf { it > 0 }?.let { Length(it) },
            siGunGuId = if (params.hasSiGunGuId()) {
                params.siGunGuId.value
            } else {
                null
            },
            eupMyeonDongId = if (params.hasEupMyeonDongId()) {
                params.eupMyeonDongId.value
            } else {
                null
            },
        )

        call.respond(
            SearchPlacesResult.newBuilder()
                .addAllItems(results.map { SearchPlacesConverter.convertItem(it) })
                .build()
        )
    }
}
