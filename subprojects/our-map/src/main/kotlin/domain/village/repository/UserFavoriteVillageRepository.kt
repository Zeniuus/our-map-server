package domain.village.repository

import domain.EntityRepository
import domain.user.entity.User
import domain.village.entity.UserFavoriteVillage
import domain.village.entity.Village

interface UserFavoriteVillageRepository : EntityRepository<UserFavoriteVillage, String> {
    fun findByUserAndVillageAndDeletedAtIsNull(user: User, village: Village): UserFavoriteVillage?
}
