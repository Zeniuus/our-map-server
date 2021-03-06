package domain.accessibility.repository

import domain.EntityRepository
import domain.accessibility.entity.PlaceAccessibility
import domain.village.entity.EupMyeonDong

interface PlaceAccessibilityRepository : EntityRepository<PlaceAccessibility, String> {
    fun findByPlaceIds(placeIds: Collection<String>): List<PlaceAccessibility>
    fun findByPlaceId(placeId: String): PlaceAccessibility?
    fun findByUserId(userId: String): List<PlaceAccessibility>
    fun countByEupMyeonDong(eupMyeonDong: EupMyeonDong): Int
    fun countByUserId(userId: String): Int
    fun countByUserIdGroupByEupMyeonDongId(userId: String): Map<String, Int>
    fun hasAccessibilityNotRegisteredPlaceInBuilding(buildingId: String): Boolean
    fun countAll(): Int
    fun listConquerRankingEntries(): List<Pair<String, Int>>
}
