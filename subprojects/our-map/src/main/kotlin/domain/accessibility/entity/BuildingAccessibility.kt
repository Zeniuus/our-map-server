package domain.accessibility.entity

import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id

@Entity
data class BuildingAccessibility(
    @Id
    @Column(length = 36, nullable = false)
    val id: String,
    @Column(length = 36, nullable = false)
    val buildingId: String,
    @Column(nullable = false)
    val hasElevator: Boolean,
    @Column(nullable = false)
    val hasObstacleToElevator: Boolean,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val stairInfo: BuildingStairInfo,
    @Column(length = 36, nullable = true)
    val userId: String?,
    @Column(nullable = false)
    val createdAt: Instant,
)
