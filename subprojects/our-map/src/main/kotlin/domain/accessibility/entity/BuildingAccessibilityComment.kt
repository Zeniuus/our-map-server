package domain.accessibility.entity

import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class BuildingAccessibilityComment(
    @Id
    @Column(nullable = false, length = 36)
    val id: String,
    @Column(nullable = false, length = 36)
    val buildingId: String,
    @Column(nullable = true, length = 36)
    val userId: String?,
    @Column(nullable = false, columnDefinition = "TEXT")
    val comment: String,
    @Column(nullable = false)
    val createdAt: Instant,
)
