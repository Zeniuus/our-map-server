package quest.domain.repository

import domain.EntityRepository
import quest.domain.entity.ClubQuest

interface ClubQuestRepository : EntityRepository<ClubQuest, String> {
    fun listNotDeleted(): List<ClubQuest>
}
