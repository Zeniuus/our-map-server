package converter

import domain.place.entity.Place
import ourMap.protocol.Model

object PlaceConverter {
    fun toProto(place: Place) = Model.Place.newBuilder()
        .setId(place.id)
        .setName(place.name)
        .setAddress(place.building.address.toString())
        .build()
}
