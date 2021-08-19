package domain.badge.entity

/**
 * 현재는 in-memory value object로 구현했지만,
 * 추후에 언제든지 DB에 저장할 수 있는 entity로 변경할 수 있도록 설계함.
 */
data class Badge(
    val id: String,
    val name: String,
    val shouldIssueToUser: (UserMetadata) -> Boolean,
) {
    data class UserMetadata(
        val buildingAccessibilityCount: Int,
    )
}
