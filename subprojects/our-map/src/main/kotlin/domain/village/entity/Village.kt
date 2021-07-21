package domain.village.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToOne

@Entity
data class Village(
    @Id
    @Column(nullable = false)
    val id: String,
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eup_myeon_dong_id", nullable = false)
    val eupMyeonDong: EupMyeonDong,
    @Column(nullable = false)
    val buildingCount: Int,
    @Column(nullable = false)
    val placeCount: Int,
    @Column(nullable = false)
    val buildingAccessibilityCount: Int,
    @Column(nullable = false)
    val placeAccessibilityCount: Int,
    @Column(nullable = false)
    val buildingAccessibilityRegisteredUserCount: Int,
    @Column(nullable = true, length = 36)
    val mostBuildingAccessibilityRegisteredUserId: String? = null,
    // TODO: 다음 채색을 위해 필요한 BuildingAccessibility 숫자
)
