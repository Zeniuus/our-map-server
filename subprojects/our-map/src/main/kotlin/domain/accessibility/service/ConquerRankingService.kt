package domain.accessibility.service

import domain.accessibility.repository.PlaceAccessibilityRepository

class ConquerRankingService(
    private val placeAccessibilityRepository: PlaceAccessibilityRepository,
) {
    private val registeredCountByUserId = mutableMapOf<String, Int>()
    private var isInitialized = false

    @Synchronized
    private fun initIfNot() {
        if (isInitialized) {
            return
        }
        val rankingEntries = placeAccessibilityRepository.listConquerRankingEntries()
        rankingEntries.forEach { (userId, registeredCount) ->
            registeredCountByUserId[userId] = registeredCount
        }
        isInitialized = true
    }

    fun getRanking(userId: String): Int? {
        initIfNot()

        val registeredCount = registeredCountByUserId[userId] ?: return null
        return registeredCountByUserId.values.count { it > registeredCount } + 1
    }

    fun updateRanking(userId: String) {
        initIfNot()

        registeredCountByUserId[userId] = placeAccessibilityRepository.countByUserId(userId)
    }
}
