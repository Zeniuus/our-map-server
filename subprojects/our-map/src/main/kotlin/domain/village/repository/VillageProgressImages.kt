package domain.village.repository

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import domain.village.entity.VillageProgressImage
import util.TextResourceReader

object VillageProgressImages {
    private val objectMapper = jacksonObjectMapper()
    private val imageByEupMyeonDongId = run {
        // TODO: 하드코딩 하지 않기
        listOf(
            "고등동.json",
            "구미동.json",
            "금곡동.json",
            "금광동.json",
            "단대동.json",
            "도촌동.json",
            "백현동.json",
            "복정동.json",
            "분당동.json",
            "산성동.json",
            "삼평동.json",
            "상대원동.json",
            "서현동.json",
            "성남동.json",
            "수내동.json",
            "수진동.json",
            "시흥동.json",
            "신촌동.json",
            "신흥동.json",
            "야탑동.json",
            "양지동.json",
            "운중동.json",
            "위례동.json",
            "은행동.json",
            "이매동.json",
            "정자동.json",
            "중앙동.json",
            "태평동.json",
            "판교동.json",
            "하대원동.json",
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
