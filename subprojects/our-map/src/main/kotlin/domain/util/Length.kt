package domain.util

data class Length(
    val meters: Double
) : Comparable<Length> {
    override operator fun compareTo(other: Length): Int {
        return when {
            meters < other.meters -> -1
            meters == other.meters -> 0
            else -> 1
        }
    }
}
