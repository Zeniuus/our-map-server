package quest.domain.repository

import domain.EntityRepository
import quest.domain.entity.ClubQuestResult

interface ClubQuestResultRepository : EntityRepository<ClubQuestResult, String> {
    fun findByQuestId(questId: String): List<ClubQuestResult>
    fun listAll(): List<ClubQuestResult>
}
