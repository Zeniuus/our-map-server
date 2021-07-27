package infra.persistence.repository

import domain.village.entity.UserFavoriteVillage
import domain.village.repository.UserFavoriteVillageRepository

class JpaUserFavoriteVillageRepository :
    UserFavoriteVillageRepository,
    JpaEntityRepositoryBase<UserFavoriteVillage, String>(UserFavoriteVillage::class.java)
