package quest.domain.entity

import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class ClubQuestResult(
    @Id
    @Column(nullable = false, length = 36)
    val id: String,
    @Column(nullable = false, length = 36)
    val questId: String,
    @Column(nullable = false, length = 128)
    val questTitle: String,
    @Column(nullable = false)
    val questCreatedAt: Instant,
    @Column(nullable = false)
    val questTargetLng: Double,
    @Column(nullable = false)
    val questTargetLat: Double,
    @Column(nullable = false, length = 128)
    val questTargetDisplayedName: String,
    @Column(nullable = false, length = 128)
    val questTargetPlaceName: String,
    @Column(nullable = false)
    var isCompleted: Boolean,
    @Column(nullable = false)
    var isClosed: Boolean,
    @Column(nullable = false)
    var isNotAccessible: Boolean,
)
