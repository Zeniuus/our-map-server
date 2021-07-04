package domain.place.entity

import domain.util.Location
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Transient

@Entity
data class Place(
    @Id
    @Column(length = 36, nullable = false)
    val id: String,
    @Column(length = 64, nullable = false)
    val name: String,
    @Column(nullable = false)
    val lng: Double,
    @Column(nullable = false)
    val lat: Double,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", nullable = false)
    val building: Building
) {
    @get:Transient
    val location: Location
        get() = Location(lng, lat)

    @Transient
    val address = building.address
}
