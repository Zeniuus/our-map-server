package application.accessibility

import application.TransactionIsolationLevel
import application.TransactionManager
import domain.accessibility.repository.BuildingAccessibilityRepository
import domain.accessibility.service.BuildingAccessibilityUpvoteService
import domain.user.repository.UserRepository

class BuildingAccessibilityUpvoteApplicationService(
    private val transactionManager: TransactionManager,
    private val userRepository: UserRepository,
    private val buildingAccessibilityRepository: BuildingAccessibilityRepository,
    private val buildingAccessibilityUpvoteService: BuildingAccessibilityUpvoteService,
) {
    fun giveUpvote(userId: String, buildingAccessibilityId: String) = transactionManager.doInTransaction(TransactionIsolationLevel.SERIALIZABLE) {
        val user = userRepository.findById(userId)
        val buildingAccessibility = buildingAccessibilityRepository.findById(buildingAccessibilityId)
        buildingAccessibilityUpvoteService.giveUpvote(user, buildingAccessibility)
    }

    fun cancelUpvote(userId: String, buildingAccessibilityId: String) = transactionManager.doInTransaction(TransactionIsolationLevel.SERIALIZABLE) {
        val user = userRepository.findById(userId)
        val buildingAccessibility = buildingAccessibilityRepository.findById(buildingAccessibilityId)
        buildingAccessibilityUpvoteService.cancelUpvote(user, buildingAccessibility)
    }
}
