package route

import application.TransactionManager
import application.place.PlaceApplicationService
import converter.BuildingConverter
import converter.PlaceConverter
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import org.koin.core.context.GlobalContext
import ourMap.protocol.ListPlacesInBuildingParams
import ourMap.protocol.ListPlacesInBuildingResult
import ourMap.protocol.SearchPlacesResult

fun Route.listPlacesInBuilding() {
    val koin = GlobalContext.get()

    val transactionManager = koin.get<TransactionManager>()
    val placeApplicationService = koin.get<PlaceApplicationService>()

    post("/listPlacesInBuilding") {
        val params = call.receive<ListPlacesInBuildingParams>()
        val results = placeApplicationService.listPlacesInBuilding(params.buildingId)

        call.respond(
            transactionManager.doInTransaction {
                ListPlacesInBuildingResult.newBuilder()
                    .addAllItems(results.map { result ->
                        SearchPlacesResult.Item.newBuilder()
                            .setPlace(PlaceConverter.toProto(result.place))
                            .setBuilding(BuildingConverter.toProto(result.place.building))
                            .setHasPlaceAccessibility(result.placeAccessibility != null)
                            .setHasBuildingAccessibility(result.buildingAccessibility != null)
                            .build()
                    })
            }
        )
    }
}
