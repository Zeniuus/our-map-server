package infra.logging.jpa

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import domain.logging.OurMapEvent
import javax.persistence.AttributeConverter

class OurMapEventJsonConverter : AttributeConverter<OurMapEvent, String> {
    private val objectMapper = jacksonObjectMapper()

    override fun convertToDatabaseColumn(attribute: OurMapEvent): String {
        return objectMapper.writeValueAsString(attribute)
    }

    override fun convertToEntityAttribute(dbData: String): OurMapEvent {
        return objectMapper.readValue(dbData, OurMapEvent::class.java)
    }
}
