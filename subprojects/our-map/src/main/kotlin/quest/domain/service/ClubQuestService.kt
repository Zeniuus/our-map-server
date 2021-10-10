package quest.domain.service

import domain.util.EntityIdGenerator
import quest.domain.entity.ClubQuest
import quest.domain.entity.ClubQuestContent
import quest.domain.repository.ClubQuestRepository
import java.time.Clock

class ClubQuestService(
    private val clock: Clock,
    private val clubQuestRepository: ClubQuestRepository,
) {
    data class CreateParams(
        val title: String,
        val content: ClubQuestContent,
    )

    fun create(params: CreateParams): ClubQuest {
        return clubQuestRepository.add(ClubQuest(
            id = EntityIdGenerator.generateRandom(),
            title = params.title.trim(),
            content = params.content,
            createdAt = clock.instant(),
        ))
    }

    fun delete(clubQuest: ClubQuest): ClubQuest {
        clubQuest.deletedAt = clock.instant()
        return clubQuestRepository.add(clubQuest)
    }
}
