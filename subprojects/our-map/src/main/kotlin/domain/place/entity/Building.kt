package domain.place.entity

import domain.util.Location

data class Building(
    val id: String,
    val name: String?,
    val location: Location,
    val address: BuildingAddress,
)
