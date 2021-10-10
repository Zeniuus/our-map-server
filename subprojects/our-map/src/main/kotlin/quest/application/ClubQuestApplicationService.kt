package quest.application

import application.TransactionManager
import infra.persistence.repository.EntityNotFoundException
import quest.domain.entity.ClubQuest
import quest.domain.repository.ClubQuestRepository
import quest.domain.service.ClubQuestService

class ClubQuestApplicationService(
    private val transactionManager: TransactionManager,
    private val clubQuestRepository: ClubQuestRepository,
    private val clubQuestService: ClubQuestService,
) {
    fun create(params: ClubQuestService.CreateParams): ClubQuest = transactionManager.doInTransaction {
        clubQuestService.create(params)
    }

    fun getSingle(id: String): ClubQuest = transactionManager.doInTransaction {
        val clubQuest = clubQuestRepository.findById(id)
        if (clubQuest.deletedAt != null) {
            throw EntityNotFoundException("이미 삭제된 퀘스트입니다.")
        }
        clubQuest
    }

    fun listAll(): List<ClubQuest> = transactionManager.doInTransaction {
        clubQuestRepository.listNotDeleted()
    }

    fun delete(id: String): ClubQuest = transactionManager.doInTransaction {
        val clubQuest = clubQuestRepository.findById(id)
        clubQuestService.delete(clubQuest)
    }
}
