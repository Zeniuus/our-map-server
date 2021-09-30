package domain.accessibility.entity

import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class PlaceAccessibilityComment(
    @Id
    @Column(nullable = false, length = 36)
    val id: String,
    @Column(nullable = false, length = 36)
    val placeId: String,
    @Column(nullable = false, length = 36)
    val userId: String?,
    @Column(nullable = false, columnDefinition = "TEXT")
    val comment: String,
    @Column(nullable = false)
    val createdAt: Instant,
)
