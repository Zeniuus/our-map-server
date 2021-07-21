package domain.village.entity

import java.math.BigDecimal
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Transient

@Entity
data class Village(
    @Id
    @Column(nullable = false)
    val id: String,
    @Column(nullable = false, length = 36)
    val eupMyeonDongId: String,
    @Column(nullable = false)
    val buildingCount: Int,
    @Column(nullable = false)
    val placeCount: Int,
    @Column(nullable = false)
    var buildingAccessibilityCount: Int,
    @Column(nullable = false)
    var placeAccessibilityCount: Int,
    @Column(nullable = false)
    var buildingAccessibilityRegisteredUserCount: Int,
    @Column(nullable = true, length = 36)
    var mostBuildingAccessibilityRegisteredUserId: String? = null,
    // TODO: 다음 채색을 위해 필요한 BuildingAccessibility 숫자
) {
    val registerProgress: BigDecimal
        @Transient get() = BigDecimal(buildingAccessibilityCount).setScale(2) / BigDecimal(buildingCount).setScale(2)
}
