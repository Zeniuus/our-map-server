package domain.placeAccessibility.repository

import domain.EntityRepository
import domain.placeAccessibility.entity.PlaceAccessibility
import domain.village.entity.EupMyeonDong

interface PlaceAccessibilityRepository : EntityRepository<PlaceAccessibility, String> {
    fun findByPlaceIds(placeIds: Collection<String>): List<PlaceAccessibility>
    fun findByPlaceId(placeId: String): PlaceAccessibility?
    fun countByEupMyeonDong(eupMyeonDong: EupMyeonDong): Int
}
