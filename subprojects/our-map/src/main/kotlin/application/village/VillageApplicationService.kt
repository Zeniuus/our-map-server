package application.village

import application.TransactionManager
import domain.village.entity.Village
import domain.village.repository.EupMyeonDongRepository
import domain.village.repository.VillageRepository
import domain.village.service.VillageService

class VillageApplicationService(
    private val transactionManager: TransactionManager,
    private val villageRepository: VillageRepository,
    private val eupMyeonDongRepository: EupMyeonDongRepository,
    private val villageService: VillageService,
) {
    fun listForMainView(): List<Village> = transactionManager.doInTransaction {
        villageRepository.listAll()
            .sortedByDescending { it.registerProgress }
    }

    fun listDropdownItems(): List<VillageDropdownItem> = transactionManager.doInTransaction {
        val eupMyeonDongById = eupMyeonDongRepository.listAll().associateBy { it.id }
        val villages = villageRepository.listAll()
        villages.map { VillageDropdownItem(village = it, eupMyeonDong = eupMyeonDongById[it.eupMyeonDongId]!!) }
            .sortedBy { it.villageName }
    }

    fun insertAll() = transactionManager.doInTransaction {
        val eupMyeonDongs = eupMyeonDongRepository.listAll()
        eupMyeonDongs.forEach { eupMyeonDong ->
            villageService.upsertStatistics(eupMyeonDong)
        }
    }
}
