package domain.badge.repository

import domain.badge.entity.Badge
import domain.util.EntityIdGenerator

class InMemoryBadgeRepository : BadgeRepository {
    private val badges = listOf(
        Badge(
            id = EntityIdGenerator.generateFixed("첫 정복 뱃지"),
            name = "첫 정복 뱃지",
            shouldIssueToUser = generateIssueConditionByBuildingAccessibilityCount(1)
        ),
        Badge(
            id = EntityIdGenerator.generateFixed("숙련자"),
            name = "숙련자",
            shouldIssueToUser = generateIssueConditionByBuildingAccessibilityCount(3)
        ),
    )

    private fun generateIssueConditionByBuildingAccessibilityCount(count: Int): (Badge.UserMetadata) -> Boolean {
        return { metadata -> metadata.buildingAccessibilityCount >= count }
    }

    override fun listAll(): List<Badge> {
        return badges
    }

    override fun findById(id: String): Badge? {
        return badges.find { it.id == id }
    }
}
