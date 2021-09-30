package domain.accessibility.repository

import domain.EntityRepository
import domain.accessibility.entity.PlaceAccessibilityComment

interface PlaceAccessibilityCommentRepository : EntityRepository<PlaceAccessibilityComment, String> {
    fun findByPlaceId(placeId: String): List<PlaceAccessibilityComment>
}
