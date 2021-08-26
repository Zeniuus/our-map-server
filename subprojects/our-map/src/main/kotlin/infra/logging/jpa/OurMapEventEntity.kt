package infra.logging.jpa

import domain.logging.OurMapEvent
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class OurMapEventEntity(
    @Id
    @Column(nullable = false, length = 36)
    val id: String,
    @Column(columnDefinition = "JSON")
    @Convert(converter = OurMapEventJsonConverter::class)
    val event: OurMapEvent,
)
