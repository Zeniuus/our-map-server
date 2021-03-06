package domain.accessibility.service

import domain.DomainException
import domain.accessibility.entity.PlaceAccessibilityComment
import domain.accessibility.repository.PlaceAccessibilityCommentRepository
import domain.place.repository.PlaceRepository
import domain.util.EntityIdGenerator
import java.time.Clock

class PlaceAccessibilityCommentService(
    private val clock: Clock,
    private val placeRepository: PlaceRepository,
    private val placeAccessibilityCommentRepository: PlaceAccessibilityCommentRepository,
) {
    data class CreateParams(
        val placeId: String,
        val userId: String?,
        val comment: String,
    )

    fun create(params: CreateParams): PlaceAccessibilityComment {
        val normalizedComment = params.comment.trim()
        if (normalizedComment.isBlank()) {
            throw DomainException("한 글자 이상의 의견을 제출해주세요.")
        }
        if (placeRepository.findByIdOrNull(params.placeId) == null) {
            throw DomainException("의견을 남기려는 장소가 없습니다.")
        }
        return placeAccessibilityCommentRepository.add(PlaceAccessibilityComment(
            id = EntityIdGenerator.generateRandom(),
            placeId = params.placeId,
            userId = params.userId,
            comment = normalizedComment,
            createdAt = clock.instant(),
        ))
    }
}
