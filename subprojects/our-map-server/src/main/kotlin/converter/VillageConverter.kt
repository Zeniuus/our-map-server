package converter

import domain.village.entity.Village
import domain.village.service.VillageService
import ourMap.protocol.Model
import java.math.BigDecimal

class VillageConverter(
    private val villageService: VillageService,
) {
    fun toProto(village: Village, isFavoriteVillage: Boolean) = Model.Village.newBuilder()
        .setId(village.id)
        .setName(villageService.getName(village))
        .setIsFavoriteVillage(isFavoriteVillage)
        .build()

    companion object {
        fun getProgressPercentage(village: Village) =
            (village.registerProgress * BigDecimal(100)).toString()
                // 소수점 부분이 0으로 끝나는 경우 처리
                .replace(Regex("0+$"), "")
                .replace(Regex("\\.$"), "")
    }
}
