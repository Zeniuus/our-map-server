package domain.accessibility.service

import domain.DomainException
import domain.accessibility.entity.BuildingAccessibilityComment
import domain.accessibility.repository.BuildingAccessibilityCommentRepository
import domain.place.repository.BuildingRepository
import domain.util.EntityIdGenerator
import java.time.Clock

class BuildingAccessibilityCommentService(
    private val clock: Clock,
    private val buildingRepository: BuildingRepository,
    private val buildingAccessibilityCommentRepository: BuildingAccessibilityCommentRepository,
) {
    data class CreateParams(
        val buildingId: String,
        val userId: String?,
        val comment: String,
    )

    fun create(params: CreateParams): BuildingAccessibilityComment {
        val normalizedComment = params.comment.trim()
        if (normalizedComment.isBlank()) {
            throw DomainException("한 글자 이상의 의견을 제출해주세요.")
        }
        if (buildingRepository.findByIdOrNull(params.buildingId) == null) {
            throw DomainException("의견을 남기려는 건물이 없습니다.")
        }
        return buildingAccessibilityCommentRepository.add(BuildingAccessibilityComment(
            id = EntityIdGenerator.generateRandom(),
            buildingId = params.buildingId,
            userId = params.userId,
            comment = normalizedComment,
            createdAt = clock.instant(),
        ))
    }
}
