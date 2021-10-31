package converter

import domain.user.entity.User
import domain.village.entity.Village
import domain.village.repository.VillageProgressImages
import domain.village.service.UserFavoriteVillageService
import domain.village.service.VillageService
import ourMap.protocol.Model
import java.math.BigDecimal

class VillageConverter(
    private val villageService: VillageService,
    private val userFavoriteVillageService: UserFavoriteVillageService,
) {
    fun toProto(village: Village, user: User?) = Model.Village.newBuilder()
        .setId(village.id)
        .setName(villageService.getName(village))
        .setIsFavoriteVillage(user?.let { userFavoriteVillageService.isFavoriteVillage(it, village) } ?: false)
        .build()!!

    fun toRankingEntryProto(
        village: Village,
        user: User?,
        progressRank: Int = villageService.getProgressRank(village)
    ) = Model.VillageRankingEntry.newBuilder()
        .setVillage(toProto(village, user))
        .setProgressRank(progressRank)
        .setProgressPercentage(getProgressPercentage(village))
        .also { builder ->
            if (progressRank <= 3) {
                VillageProgressImages.getImage(village.eupMyeonDongId)?.let { image ->
                    builder.progressImage = Model.VillageRankingEntry.VillageProgressImage.newBuilder()
                        .setId(image.id)
                        .setNumberOfBlocks(image.numberOfBlocks)
                        .addAllPaths(image.paths.map { path ->
                            Model.VillageRankingEntry.VillageProgressImage.Path.newBuilder()
                                .setType(path.type)
                                .putAllProps(path.props)
                                .build()
                        })
                        .build()
                }
            }
        }
        .build()!!

    companion object {
        fun getProgressPercentage(village: Village) =
            (village.registerProgress * BigDecimal(100)).toString()
                // 소수점 부분이 0으로 끝나는 경우 처리
                .replace(Regex("0+$"), "")
                .replace(Regex("\\.$"), "")
    }
}
