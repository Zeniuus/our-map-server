package application.village

import application.TransactionIsolationLevel
import application.TransactionManager
import domain.user.repository.UserRepository
import domain.village.entity.Village
import domain.village.repository.EupMyeonDongRepository
import domain.village.repository.VillageRepository
import domain.village.service.UserFavoriteVillageService
import domain.village.service.VillageService

class VillageApplicationService(
    private val transactionManager: TransactionManager,
    private val userRepository: UserRepository,
    private val villageRepository: VillageRepository,
    private val eupMyeonDongRepository: EupMyeonDongRepository,
    private val villageService: VillageService,
    private val userFavoriteVillageService: UserFavoriteVillageService,
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

    fun getStatistics(userId: String, villageId: String): VillageStatistics = transactionManager.doInTransaction {
        val user = userRepository.findById(userId)
        val village = villageRepository.findById(villageId)
        VillageStatistics(
            village = village,
            eupMyeonDong = eupMyeonDongRepository.findById(village.eupMyeonDongId),
            progressRank = villageService.getProgressRank(village),
            isFavoriteVillage = userFavoriteVillageService.isFavoriteVillage(user, village),
            mostRegisteredUser = village.mostBuildingAccessibilityRegisteredUserId?.let {
                userRepository.findById(it)
            },
        )
    }

    fun upsertAll() = transactionManager.doInTransaction {
        val eupMyeonDongIds = transactionManager.doInTransaction {
            eupMyeonDongRepository.listAll().map { it.id }
        }
        eupMyeonDongIds.forEach { eupMyeonDongId ->
            try {
                transactionManager.doInTransaction(TransactionIsolationLevel.SERIALIZABLE) {
                    val eupMyeonDong = eupMyeonDongRepository.findById(eupMyeonDongId)
                    villageService.upsertStatistics(eupMyeonDong)
                }
            } catch (t: Throwable) {
                // TODO: 에러 로깅
            }
        }
    }
}
