package route.place

import application.place.PlaceApplicationService
import domain.util.Location
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import org.koin.core.context.GlobalContext
import ourMap.protocol.SearchPlacesParams
import ourMap.protocol.SearchPlacesResult
import route.UserAuthenticator
import route.converter.BuildingAccessibilityConverter
import route.converter.PlaceAccessibilityConverter
import route.converter.PlaceConverter

fun Route.placeRoutes() {
    val koin = GlobalContext.getKoinApplicationOrNull()!!.koin
    val placeApplicationService = koin.get<PlaceApplicationService>()
    val userAuthenticator = koin.get<UserAuthenticator>()

    post("/searchPlaces") {
        userAuthenticator.checkAuth(call.request)

        val params = call.receive<SearchPlacesParams>()
        val results = placeApplicationService.searchPlaces(
            searchText = params.searchText,
            location = Location(
                lng = params.currentLocation.lng,
                lat = params.currentLocation.lat,
            ),
        )

        call.respond(
            SearchPlacesResult.newBuilder()
                .addAllItems(results.map { result ->
                    SearchPlacesResult.Item.newBuilder()
                        .setPlace(PlaceConverter.toProto(result.place))
                        .also {
                            if (result.placeAccessibility != null) {
                                it.placeAccessibility = PlaceAccessibilityConverter.toProto(result.placeAccessibility!!)
                            }
                        }
                        .also {
                            if (result.buildingAccessibility != null) {
                                it.buildingAccessibility = BuildingAccessibilityConverter.toProto(result.buildingAccessibility!!)
                            }
                        }
                        .build()
                })
        )
    }
}
