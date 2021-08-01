package infra.persistence.repository

import domain.user.entity.User
import domain.village.entity.UserFavoriteVillage
import domain.village.entity.Village
import domain.village.repository.UserFavoriteVillageRepository
import infra.persistence.transaction.EntityManagerHolder

class JpaUserFavoriteVillageRepository :
    UserFavoriteVillageRepository,
    JpaEntityRepositoryBase<UserFavoriteVillage, String>(UserFavoriteVillage::class.java) {
    override fun findByUserAndVillageAndNotDeleted(user: User, village: Village): UserFavoriteVillage? {
        val em = EntityManagerHolder.get()!!
        val query = em.createQuery("""
            SELECT v
            FROM UserFavoriteVillage v
            WHERE
                v.userId = :userId
                AND v.village = :village
                AND v.deletedAt IS NULL
        """.trimIndent(), UserFavoriteVillage::class.java)
        query.setParameter("userId", user.id)
        query.setParameter("village", village)
        return getSingularResultOrThrow(query.resultList)
    }

    override fun findByUserAndNotDeleted(user: User): List<UserFavoriteVillage> {
        val em = EntityManagerHolder.get()!!
        val query = em.createQuery("""
            SELECT v
            FROM UserFavoriteVillage v
            JOIN FETCH v.village village
            WHERE
                v.userId = :userId
                AND v.deletedAt IS NULL
        """.trimIndent(), UserFavoriteVillage::class.java)
        query.setParameter("userId", user.id)
        return query.resultList
    }
}
