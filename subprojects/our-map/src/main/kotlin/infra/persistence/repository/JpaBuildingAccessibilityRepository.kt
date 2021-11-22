package infra.persistence.repository

import domain.accessibility.entity.BuildingAccessibility
import domain.accessibility.repository.BuildingAccessibilityRepository
import domain.village.entity.EupMyeonDong
import infra.persistence.transaction.EntityManagerHolder
import java.math.BigInteger

class JpaBuildingAccessibilityRepository :
    JpaEntityRepositoryBase<BuildingAccessibility, String>(BuildingAccessibility::class.java),
    BuildingAccessibilityRepository {
    override fun findByBuildingIds(buildingIds: Collection<String>): List<BuildingAccessibility> {
        val em = EntityManagerHolder.get()!!
        val query = em.createQuery("""
            SELECT ba
            FROM BuildingAccessibility ba
            WHERE ba.buildingId IN :buildingIds
        """.trimIndent(), BuildingAccessibility::class.java)
        query.setParameter("buildingIds", buildingIds)
        return query.resultList
    }

    override fun findByBuildingId(buildingId: String): BuildingAccessibility? {
        val em = EntityManagerHolder.get()!!
        val query = em.createQuery("""
            SELECT ba
            FROM BuildingAccessibility ba
            WHERE ba.buildingId = :buildingId
        """.trimIndent(), BuildingAccessibility::class.java)
        query.setParameter("buildingId", buildingId)
        return getSingularResultOrThrow(query.resultList)
    }

    override fun findByEupMyeonDong(eupMyeonDong: EupMyeonDong): List<BuildingAccessibility> {
        val em = EntityManagerHolder.get()!!
        val query = em.createNativeQuery("""
            SELECT *
            FROM building_accessibility ba
            LEFT OUTER JOIN building ON building.id = ba.building_id
            WHERE building.eup_myeon_dong_id = :eupMyeonDongId
        """.trimIndent(), BuildingAccessibility::class.java)
        query.setParameter("eupMyeonDongId", eupMyeonDong.id)
        return query.resultList.map { it as BuildingAccessibility }
    }

    override fun countByUserId(userId: String): Int {
        val em = EntityManagerHolder.get()!!
        val query = em.createNativeQuery("""
            SELECT COUNT(*)
            FROM building_accessibility ba
            WHERE user_id = :userId
        """.trimIndent())
        query.setParameter("userId", userId)
        return (query.singleResult as BigInteger).toInt()
    }
}
