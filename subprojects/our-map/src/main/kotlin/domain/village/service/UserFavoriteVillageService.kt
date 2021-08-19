package domain.village.service

import domain.user.entity.User
import domain.util.EntityIdGenerator
import domain.village.entity.UserFavoriteVillage
import domain.village.entity.Village
import domain.village.repository.UserFavoriteVillageRepository
import java.time.Clock

class UserFavoriteVillageService(
    private val clock: Clock,
    private val userFavoriteVillageRepository: UserFavoriteVillageRepository,
) {
    fun isFavoriteVillage(user: User, village: Village): Boolean {
        return userFavoriteVillageRepository.findByUserAndVillageAndNotDeleted(user, village) != null
    }

    fun register(user: User, village: Village): UserFavoriteVillage {
        val existingOne = userFavoriteVillageRepository.findByUserAndVillageAndNotDeleted(user, village)
        if (existingOne != null) {
            return existingOne
        }

        return userFavoriteVillageRepository.add(UserFavoriteVillage(
            id = EntityIdGenerator.generateRandom(),
            userId = user.id,
            village = village,
            createdAt = clock.instant(),
        ))
    }

    fun unregister(user: User, village: Village) {
        userFavoriteVillageRepository.findByUserAndVillageAndNotDeleted(user, village)?.let {
            it.deletedAt = clock.instant()
            userFavoriteVillageRepository.add(it)
        }
    }
}
