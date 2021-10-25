package quest.domain.entity

data class ClubQuestContent(
    val targets: List<Target>
) {
    data class Target(
        val lng: Double,
        val lat: Double,
        val displayedName: String,
        val places: List<Place> = emptyList(),
    ) {
        data class Place(
            val name: String,
            var isCompleted: Boolean = false,
            var isClosed: Boolean = false,
            var isNotAccessible: Boolean = false
        )
    }
}
