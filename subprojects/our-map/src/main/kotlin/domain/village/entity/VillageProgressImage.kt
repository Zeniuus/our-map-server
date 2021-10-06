package domain.village.entity

data class VillageProgressImage(
    val id: String,
    val eupMyeonDongId: String,
    val numberOfBlocks: Int,
    val paths: List<Path>
) {
    data class Path(
        val type: String,
        val props: Map<String, String>
    )
}
