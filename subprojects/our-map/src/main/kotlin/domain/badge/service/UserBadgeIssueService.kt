package domain.badge.service

import domain.accessibility.repository.BuildingAccessibilityRepository
import domain.badge.entity.Badge
import domain.badge.entity.UserBadgeIssue
import domain.badge.repository.BadgeRepository
import domain.badge.repository.UserBadgeIssueRepository
import domain.user.entity.User
import domain.util.EntityIdGenerator
import java.time.Clock

class UserBadgeIssueService(
    private val clock: Clock,
    private val badgeRepository: BadgeRepository,
    private val userBadgeIssueRepository: UserBadgeIssueRepository,
    private val buildingAccessibilityRepository: BuildingAccessibilityRepository,
) {
    fun findBadgesToIssue(user: User): List<Badge> {
        val issuedBadgeIds = userBadgeIssueRepository.findByUser(user).map { it.badgeId }.toSet()
        val userMetadata = createUserMetadata(user)
        val issuableBadges = badgeRepository.listAll().filter { it.shouldIssueToUser(userMetadata) }
        return issuableBadges.filter { it.id !in issuedBadgeIds }
    }

    private fun createUserMetadata(user: User): Badge.UserMetadata {
        return Badge.UserMetadata(
            buildingAccessibilityCount = buildingAccessibilityRepository.countByUserId(user.id),
        )
    }

    fun issueBadge(user: User, badge: Badge): UserBadgeIssue {
        return userBadgeIssueRepository.add(UserBadgeIssue(
            id = EntityIdGenerator.generateRandom(),
            userId = user.id,
            badgeId = badge.id,
            createdAt = clock.instant(),
        ))
    }
}
