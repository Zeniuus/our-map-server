package domain.village.repository

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import domain.village.entity.VillageProgressImage
import util.TextResourceReader

object VillageProgressImages {
    private val objectMapper = jacksonObjectMapper()
    private val imageByEupMyeonDongId = run {
        val node = objectMapper.readTree(TextResourceReader.read("village_progress_images.json"))
        node.elements().asSequence().toList()
            .map { objectMapper.readValue(it.toString(), VillageProgressImage::class.java) }
            .associateBy { it.eupMyeonDongId }
    }

    fun getImage(eupMyeonDongId: String): VillageProgressImage? {
        return imageByEupMyeonDongId[eupMyeonDongId]
    }
}
