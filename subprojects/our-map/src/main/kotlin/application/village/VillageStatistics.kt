package application.village

import domain.user.entity.User
import domain.village.entity.EupMyeonDong
import domain.village.entity.Village

data class VillageStatistics(
    val village: Village,
    val eupMyeonDong: EupMyeonDong,
    val progressRank: Int,
    val isFavoriteVillage: Boolean,
    val mostRegisteredUser: User?,
)
