package quest.domain.entity

import java.time.Instant
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class ClubQuest(
    @Id
    @Column(nullable = false, length = 36)
    val id: String,

    @Column(nullable = false, length = 128)
    val title: String,

    @Convert(converter = ClubQuestContentAttributeConverter::class)
    @Column(nullable = false, columnDefinition = "JSON")
    val content: ClubQuestContent,

    @Column(nullable = false)
    val createdAt: Instant,

    @Column(nullable = true)
    var deletedAt: Instant? = null,
)
