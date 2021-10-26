package domain.village.repository

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import domain.village.entity.VillageProgressImage
import util.TextResourceReader

object VillageProgressImages {
    private val objectMapper = jacksonObjectMapper()
    private val imageByEupMyeonDongId = run {
        // TODO: 하드코딩 하지 않기
        listOf(
            "구미동.json",
            "금곡동.json",
            "서현동.json",
            "성남동.json",
            "수진동.json",
            "야탑동.json",
            "이매동.json",
            "정자동.json",
            "중앙동.json",
        )
            .map { filename ->
                val jsonText = TextResourceReader.read("village-progress-images/$filename")
                objectMapper.readValue(jsonText, VillageProgressImage::class.java)
            }
            .associateBy { it.eupMyeonDongId }
    }

    fun getImage(eupMyeonDongId: String): VillageProgressImage? {
        return imageByEupMyeonDongId[eupMyeonDongId]
    }
}
