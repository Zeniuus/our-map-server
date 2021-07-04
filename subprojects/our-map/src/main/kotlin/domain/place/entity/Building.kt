package domain.place.entity

import domain.util.Location
import javax.persistence.Column
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Transient

@Entity
data class Building(
    @Id
    @Column(length = 36, nullable = false)
    val id: String,
    @Column(length = 64, nullable = true)
    val name: String?,
    @Column(nullable = false)
    private val lng: Double,
    @Column(nullable = false)
    private val lat: Double,
    @Embedded
    val address: BuildingAddress,
) {
    @get:Transient
    val location: Location
        get() = Location(lng, lat)
}
