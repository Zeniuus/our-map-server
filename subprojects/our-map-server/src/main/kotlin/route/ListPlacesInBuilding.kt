package route

import application.place.PlaceApplicationService
import converter.SearchPlacesConverter
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import org.koin.core.context.GlobalContext
import ourMap.protocol.ListPlacesInBuildingParams
import ourMap.protocol.ListPlacesInBuildingResult

fun Route.listPlacesInBuilding() {
    val koin = GlobalContext.get()

    val placeApplicationService = koin.get<PlaceApplicationService>()

    post("/listPlacesInBuilding") {
        val params = call.receive<ListPlacesInBuildingParams>()
        val results = placeApplicationService.listPlacesInBuilding(params.buildingId)

        call.respond(
            ListPlacesInBuildingResult.newBuilder()
                .addAllItems(results.map { SearchPlacesConverter.convertItem(it) })
                .build()
        )
    }
}
