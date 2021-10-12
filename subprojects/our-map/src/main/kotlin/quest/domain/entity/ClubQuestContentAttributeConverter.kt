package quest.domain.entity

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import javax.persistence.AttributeConverter

class ClubQuestContentAttributeConverter : AttributeConverter<ClubQuestContent, String> {
    private val objectMapper = jacksonObjectMapper()

    override fun convertToDatabaseColumn(attribute: ClubQuestContent): String {
        return objectMapper.writeValueAsString(attribute)
    }

    override fun convertToEntityAttribute(dbData: String): ClubQuestContent {
        return objectMapper.readValue(dbData, ClubQuestContent::class.java)
    }
}
