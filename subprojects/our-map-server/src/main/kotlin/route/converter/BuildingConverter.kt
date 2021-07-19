package route.converter

import domain.place.entity.Building
import ourMap.protocol.Model

object BuildingConverter {
    fun toProto(building: Building) = Model.Building.newBuilder()
        .setId(building.id)
        .setAddress(building.address.toString())
        .build()
}
