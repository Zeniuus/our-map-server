package quest.domain.entity

data class ClubQuestContent(
    val targets: List<Target>
) {
    data class Target(
        val lng: Double,
        val lat: Double,
        val displayedName: String,
    )
}
