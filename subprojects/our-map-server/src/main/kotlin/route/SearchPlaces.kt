package route

import application.place.PlaceApplicationService
import auth.UserAuthenticator
import converter.BuildingConverter
import converter.PlaceConverter
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
                .addAllItems(results.map { result ->
                    SearchPlacesResult.Item.newBuilder()
                        .setPlace(PlaceConverter.toProto(result.place))
                        .setBuilding(BuildingConverter.toProto(result.place.building))
                        .setHasPlaceAccessibility(result.placeAccessibility != null)
                        .setHasBuildingAccessibility(result.buildingAccessibility != null)
                        .build()
                })
        )
    }
}
