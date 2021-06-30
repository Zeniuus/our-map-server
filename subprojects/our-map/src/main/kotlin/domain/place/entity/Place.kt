package domain.place.entity

import domain.util.Location

data class Place(
    val id: String,
    val name: String,
    val location: Location,
    val building: Building
) {
    val address = building.address
}
