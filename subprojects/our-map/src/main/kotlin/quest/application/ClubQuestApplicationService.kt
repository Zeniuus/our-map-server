package quest.application

import application.TransactionIsolationLevel
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

    fun setPlaceIsCompleted(id: String, targetPlaceInfo: ClubQuestService.ClubQuestTargetPlaceInfo, isCompleted: Boolean): ClubQuest = transactionManager.doInTransaction(TransactionIsolationLevel.SERIALIZABLE) {
        val clubQuest = clubQuestRepository.findById(id)
        clubQuestService.setPlaceIsCompleted(clubQuest, targetPlaceInfo, isCompleted)
    }

    fun setPlaceIsClosed(id: String, targetPlaceInfo: ClubQuestService.ClubQuestTargetPlaceInfo, isClosed: Boolean): ClubQuest = transactionManager.doInTransaction(TransactionIsolationLevel.SERIALIZABLE) {
        val clubQuest = clubQuestRepository.findById(id)
        clubQuestService.setPlaceIsClosed(clubQuest, targetPlaceInfo, isClosed)
    }

    fun setPlaceIsNotAccessible(id: String, targetPlaceInfo: ClubQuestService.ClubQuestTargetPlaceInfo, isNotAccessible: Boolean): ClubQuest = transactionManager.doInTransaction(TransactionIsolationLevel.SERIALIZABLE) {
        val clubQuest = clubQuestRepository.findById(id)
        clubQuestService.setPlaceIsNotAccessible(clubQuest, targetPlaceInfo, isNotAccessible)
    }

    fun listAll(): List<ClubQuest> = transactionManager.doInTransaction {
        clubQuestRepository.listNotDeleted()
    }

    fun delete(id: String): ClubQuest = transactionManager.doInTransaction {
        val clubQuest = clubQuestRepository.findById(id)
        clubQuestService.delete(clubQuest)
    }
}
