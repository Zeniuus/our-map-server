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
}
