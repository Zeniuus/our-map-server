package application.village

import application.TransactionIsolationLevel
import application.TransactionManager
import domain.user.repository.UserRepository
import domain.village.repository.VillageRepository
import domain.village.service.UserFavoriteVillageService

class UserFavoriteVillageApplicationService(
    private val transactionManager: TransactionManager,
    private val userRepository: UserRepository,
    private val villageRepository: VillageRepository,
    private val userFavoriteVillageService: UserFavoriteVillageService,
) {
    fun register(userId: String, villageId: String) = transactionManager.doInTransaction(TransactionIsolationLevel.SERIALIZABLE) {
        val user = userRepository.findById(userId)
        val village = villageRepository.findById(villageId)
        userFavoriteVillageService.register(user, village)
    }

    fun unregister(userId: String, villageId: String) = transactionManager.doInTransaction(TransactionIsolationLevel.SERIALIZABLE) {
        val user = userRepository.findById(userId)
        val village = villageRepository.findById(villageId)
        userFavoriteVillageService.unregister(user, village)
    }
}
