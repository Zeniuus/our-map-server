package domain.accessibility.entity

import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
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
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    val stairInfo: StairInfo,
    @Column(nullable = false)
    val hasSlope: Boolean,
    @Column(length = 36, nullable = true)
    val userId: String?,
    @Column(nullable = false)
    val createdAt: Instant,
)
