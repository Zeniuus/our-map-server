package converter

import application.place.PlaceApplicationService
import ourMap.protocol.Common
import ourMap.protocol.SearchPlacesResult

object SearchPlacesConverter {
    fun convertItem(result: PlaceApplicationService.SearchPlaceResult): SearchPlacesResult.Item {
        return SearchPlacesResult.Item.newBuilder()
            .setPlace(PlaceConverter.toProto(result.place))
            .setBuilding(BuildingConverter.toProto(result.place.building))
            .setHasPlaceAccessibility(result.placeAccessibility != null)
            .setHasBuildingAccessibility(result.buildingAccessibility != null)
            .apply {
                if (result.distance != null) {
                    distanceMeters = Common.Int32Value.newBuilder().setValue(result.distance!!.meters.toInt()).build()
                }
            }
            .build()
    }
}
