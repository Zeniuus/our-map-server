package domain.placeAccessibility.entity

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
    val hasObsticleToElevator: Boolean,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val starInfo: BuildingStairInfo,
    @Column(length = 36, nullable = false)
    val userId: String,
)
