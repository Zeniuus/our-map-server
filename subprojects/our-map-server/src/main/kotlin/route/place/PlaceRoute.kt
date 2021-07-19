package route.place

import application.place.PlaceApplicationService
import domain.util.Location
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import org.koin.core.context.GlobalContext
import ourMap.protocol.GetAccessibilityParams
import ourMap.protocol.GetAccessibilityResult
import ourMap.protocol.SearchPlacesParams
import ourMap.protocol.SearchPlacesResult
import route.UserAuthenticator
import route.converter.BuildingAccessibilityConverter
import route.converter.BuildingConverter
import route.converter.PlaceAccessibilityConverter
import route.converter.PlaceConverter

fun Route.placeRoutes() {
    val koin = GlobalContext.getKoinApplicationOrNull()!!.koin
    val placeApplicationService = koin.get<PlaceApplicationService>()
    val userAuthenticator = koin.get<UserAuthenticator>()
    val placeAccessibilityConverter = koin.get<PlaceAccessibilityConverter>()
    val buildingAccessibilityConverter = koin.get<BuildingAccessibilityConverter>()

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

    post("/getAccessibility") {
        userAuthenticator.checkAuth(call.request)

        val params = call.receive<GetAccessibilityParams>()
        val (placeAccessibility, buildingAccessibility) = placeApplicationService.getAccessibility(params.placeId)

        call.respond(
            GetAccessibilityResult.newBuilder()
                .also {
                    if (placeAccessibility != null) {
                        it.placeAccessibility = placeAccessibilityConverter.toProto(placeAccessibility)
                    }
                }
                .also {
                    if (buildingAccessibility != null) {
                        it.buildingAccessibility = buildingAccessibilityConverter.toProto(buildingAccessibility)
                    }
                }
                .build()
        )
    }
}
