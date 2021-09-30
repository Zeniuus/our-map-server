package domain.accessibility.service

import domain.DomainException
import domain.accessibility.entity.PlaceAccessibilityComment
import domain.accessibility.repository.PlaceAccessibilityCommentRepository
import domain.place.entity.Place
import domain.user.entity.User
import domain.util.EntityIdGenerator
import java.time.Clock

class PlaceAccessibilityCommentService(
    private val clock: Clock,
    private val placeAccessibilityCommentRepository: PlaceAccessibilityCommentRepository,
) {
    data class CreateParams(
        val place: Place,
        val user: User?,
        val comment: String,
    )

    fun create(params: CreateParams): PlaceAccessibilityComment {
        val normalizedComment = params.comment.trim()
        if (normalizedComment.isBlank()) {
            throw DomainException("한 글자 이상의 의견을 제출해주세요.")
        }
        return placeAccessibilityCommentRepository.add(PlaceAccessibilityComment(
            id = EntityIdGenerator.generateRandom(),
            placeId = params.place.id,
            userId = params.user?.id,
            comment = normalizedComment,
            createdAt = clock.instant(),
        ))
    }
}
