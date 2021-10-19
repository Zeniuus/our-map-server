package domain.place.service

import domain.place.entity.Place
import domain.place.repository.PlaceRepository
import domain.util.Length
import domain.util.Location
import domain.util.LocationUtils
import domain.village.entity.EupMyeonDong
import domain.village.entity.SiGunGu

class SearchPlaceService(
    private val placeRepository: PlaceRepository
) {
    data class SearchOption(
        val searchText: String,
        val location: Location?,
        val maxDistance: Length? = null,
        val siGunGu: SiGunGu? = null,
        val eupMyeonDong: EupMyeonDong? = null,
    )

    data class PlaceWithMetadata(
        val place: Place,
        val distance: Length?,
    )

    fun searchPlaces(searchOption: SearchOption): List<PlaceWithMetadata> {
        val trimmedSearchText = searchOption.searchText.trim()
        // 검색어가 너무 짧으면 검색을 하지 않는다.
        if (trimmedSearchText.length <= 1) {
            return emptyList()
        }

        val searchTextRegex = getSQLSearchTextRegex(trimmedSearchText)
        return placeRepository.findByNameContains(searchTextRegex)
            .asSequence()
            .map { place -> PlaceWithMetadata(place, searchOption.location?.let { LocationUtils.calculateDistance(it, place.location) }) }
            .filter { searchOption.siGunGu == null || it.place.siGunGuId == searchOption.siGunGu.id }
            .filter { searchOption.eupMyeonDong == null || it.place.eupMyeonDongId == searchOption.eupMyeonDong.id }
            .filter { searchOption.maxDistance == null || (it.distance ?: Length(0.0)) <= searchOption.maxDistance }
            .let { result ->
                if (searchOption.location != null) {
                    result.sortedBy { it.distance!! }
                } else {
                    result
                }
            }
            .toList()
    }

    /**
     * 한글 검색이고 길이가 충분히 긴 경우, 한 글자 정도 틀린 경우를 고려해준다.
     * e.g. '파리바게트'로 검색해도 '파리바게뜨'가 검색 결과에 뜨게 한다.
     */
    fun getSQLSearchTextRegex(normalizedSearchText: String): String {
        return if (normalizedSearchText.count { it.isKoreanCharacter() && it != ' ' } >= 3) {
            val result = mutableListOf<String>()
            normalizedSearchText.forEachIndexed { idx, c ->
                if (c.isKoreanCharacter() && c != ' ') {
                    result.add(normalizedSearchText.replaceRange(idx, idx + 1, "."))
                }
            }
            result
        } else {
            listOf(normalizedSearchText)
        }
            .map { it.replace(" ", " ?") }
            .joinToString("|")
    }

    private val hangulUnicodeBlocks = setOf(
        Character.UnicodeBlock.HANGUL_JAMO,
        Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO,
        Character.UnicodeBlock.HANGUL_SYLLABLES,
        Character.UnicodeBlock.HANGUL_JAMO_EXTENDED_A,
        Character.UnicodeBlock.HANGUL_JAMO_EXTENDED_B
    )
    private fun Char.isKoreanCharacter(): Boolean {
        return Character.UnicodeBlock.of(this) in hangulUnicodeBlocks
    }
}
