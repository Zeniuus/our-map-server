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
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    val entranceStairInfo: StairInfo,
    @Column(nullable = false)
    val hasSlope: Boolean,
    @Column(nullable = false)
    val hasElevator: Boolean,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    val elevatorStairInfo: StairInfo,
    @Column(length = 36, nullable = true)
    val userId: String?,
    @Column(nullable = false)
    val createdAt: Instant,
)
