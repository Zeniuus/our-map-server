package application.village

import application.TransactionManager
import domain.village.entity.Village
import domain.village.service.VillageService

class VillageApplicationService(
    private val transactionManager: TransactionManager,
    private val villageService: VillageService,
) {
    fun listForMainView(): List<Village> = transactionManager.doInTransaction {
        villageService.listByRanking()
    }
}
