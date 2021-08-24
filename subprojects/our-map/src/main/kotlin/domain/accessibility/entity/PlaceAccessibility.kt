package domain.accessibility.entity

import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class PlaceAccessibility(
    @Id
    @Column(length = 36, nullable = false)
    val id: String,
    @Column(length = 36, nullable = false)
    val placeId: String,
    @Column(nullable = false)
    val isFirstFloor: Boolean,
    @Column(nullable = false)
    val hasStair: Boolean,
    @Column(nullable = false)
    val isWheelchairAccessible: Boolean,
    @Column(length = 36, nullable = true)
    val userId: String?,
    @Column(nullable = false)
    val createdAt: Instant,
)
