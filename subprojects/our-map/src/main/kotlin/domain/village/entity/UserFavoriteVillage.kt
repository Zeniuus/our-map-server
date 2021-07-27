package domain.village.entity

import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
data class UserFavoriteVillage(
    @Id
    @Column(nullable = false, length = 36)
    val id: String,
    @Column(nullable = false, length = 36)
    val userId: String,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "village_id", nullable = false)
    val village: Village,
    @Column(nullable = false)
    val createdAt: Instant,
    @Column(nullable = true)
    var deletedAt: Instant? = null
)
