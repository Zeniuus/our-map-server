package converter

import domain.village.entity.Village
import domain.village.service.VillageService
import ourMap.protocol.Model

class VillageConverter(
    private val villageService: VillageService,
) {
    fun toProto(village: Village, isFavoriteVillage: Boolean) = Model.Village.newBuilder()
        .setId(village.id)
        .setName(villageService.getName(village))
        .setIsFavoriteVillage(isFavoriteVillage)
        .build()
}
