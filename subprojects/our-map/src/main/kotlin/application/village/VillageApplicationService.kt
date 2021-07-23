package application.village

import application.TransactionManager
import domain.village.entity.Village
import domain.village.repository.VillageRepository

class VillageApplicationService(
    private val transactionManager: TransactionManager,
    private val villageRepository: VillageRepository,
) {
    fun listForMainView(): List<Village> = transactionManager.doInTransaction {
        villageRepository.listAll()
            .sortedByDescending { it.registerProgress }
    }
}
