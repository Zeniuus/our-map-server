package infra.persistence.repository

import domain.placeAccessibility.entity.BuildingAccessibility
import domain.placeAccessibility.entity.BuildingAccessibilityUpvote
import domain.placeAccessibility.repository.BuildingAccessibilityUpvoteRepository
import domain.user.entity.User
import infra.persistence.transaction.EntityManagerHolder

class JpaBuildingAccessibilityUpvoteRepository :
    JpaEntityRepositoryBase<BuildingAccessibilityUpvote, String>(BuildingAccessibilityUpvote::class.java),
    BuildingAccessibilityUpvoteRepository {
    override fun findByUserAndBuildingAccessibilityAndNotDeleted(
        user: User,
        buildingAccessibility: BuildingAccessibility
    ): BuildingAccessibilityUpvote? {
        val em = EntityManagerHolder.get()!!
        val query = em.createQuery("""
            SELECT bau
            FROM BuildingAccessibilityUpvote bau
            WHERE
                bau.userId = :userId
                AND bau.buildingAccessibility = :buildingAccessibility
                AND bau.deletedAt IS NULL
        """.trimIndent(), BuildingAccessibilityUpvote::class.java)
        query.setParameter("userId", user.id)
        query.setParameter("buildingAccessibility", buildingAccessibility)
        return getSingularResultOrThrow(query.resultList)
    }

    override fun getTotalUpvoteCount(user: User): Int {
        val em = EntityManagerHolder.get()!!
        val query = em.createQuery("""
            SELECT COUNT(bau.id)
            FROM BuildingAccessibilityUpvote bau
            LEFT OUTER JOIN bau.buildingAccessibility ba
            WHERE
                ba.userId = :userId
                AND bau.deletedAt IS NULL
        """.trimIndent())
        query.setParameter("userId", user.id)
        return (query.singleResult as java.lang.Long).toInt()
    }

    override fun getTotalUpvoteCount(buildingAccessibility: BuildingAccessibility): Int {
        val em = EntityManagerHolder.get()!!
        val query = em.createQuery("""
            SELECT COUNT(bau.id)
            FROM BuildingAccessibilityUpvote bau
            WHERE
                bau.buildingAccessibility = :buildingAccessibility
                AND bau.deletedAt IS NULL
        """.trimIndent())
        query.setParameter("buildingAccessibility", buildingAccessibility)
        return (query.singleResult as java.lang.Long).toInt()
    }
}
