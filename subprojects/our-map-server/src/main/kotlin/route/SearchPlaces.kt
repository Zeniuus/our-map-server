package route

import application.place.PlaceApplicationService
import auth.UserAuthenticator
import converter.BuildingConverter
import converter.PlaceConverter
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
            // TODO: 거리 조건, 시군구 / 읍면동 조건 고려하기
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
