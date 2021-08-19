package domain.badge.entity

import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class UserBadgeIssue(
    @Id
    @Column(nullable = false, length = 36)
    val id: String,
    @Column(nullable = false, length = 36)
    val userId: String,
    @Column(nullable = false, length = 36)
    val badgeId: String,
    @Column(nullable = false)
    val createdAt: Instant,
)
