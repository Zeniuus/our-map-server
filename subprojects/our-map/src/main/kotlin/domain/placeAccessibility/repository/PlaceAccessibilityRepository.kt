package domain.placeAccessibility.repository

import domain.EntityRepository
import domain.placeAccessibility.entity.PlaceAccessibility

interface PlaceAccessibilityRepository : EntityRepository<PlaceAccessibility, String> {
    fun findByPlaceIds(placeIds: Collection<String>): List<PlaceAccessibility>
}
