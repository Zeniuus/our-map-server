package infra.persistence.repository

import domain.accessibility.entity.PlaceAccessibility
import domain.accessibility.repository.PlaceAccessibilityRepository
import domain.village.entity.EupMyeonDong
import infra.persistence.transaction.EntityManagerHolder
import java.math.BigInteger

class JpaPlaceAccessibilityRepository :
    JpaEntityRepositoryBase<PlaceAccessibility, String>(PlaceAccessibility::class.java),
    PlaceAccessibilityRepository {
    override fun findByPlaceIds(placeIds: Collection<String>): List<PlaceAccessibility> {
        val em = EntityManagerHolder.get()!!
        val query = em.createQuery("""
            SELECT pa
            FROM PlaceAccessibility pa
            WHERE pa.placeId IN :placeIds
        """.trimIndent(), PlaceAccessibility::class.java)
        query.setParameter("placeIds", placeIds)
        return query.resultList
    }

    override fun findByPlaceId(placeId: String): PlaceAccessibility? {
        val em = EntityManagerHolder.get()!!
        val query = em.createQuery("""
            SELECT pa
            FROM PlaceAccessibility pa
            WHERE pa.placeId = :placeId
        """.trimIndent(), PlaceAccessibility::class.java)
        query.setParameter("placeId", placeId)
        return getSingularResultOrThrow(query.resultList)
    }

    override fun countByEupMyeonDong(eupMyeonDong: EupMyeonDong): Int {
        val em = EntityManagerHolder.get()!!
        val query = em.createNativeQuery("""
            SELECT COUNT(*)
            FROM place_accessibility pa
            LEFT OUTER JOIN place ON place.id = pa.place_id
            WHERE place.eup_myeon_dong_id = :eupMyeonDongId
        """.trimIndent())
        query.setParameter("eupMyeonDongId", eupMyeonDong.id)
        return (query.singleResult as BigInteger).toInt()
    }

    override fun countByUserId(userId: String): Int {
        val em = EntityManagerHolder.get()!!
        val query = em.createNativeQuery("""
            SELECT COUNT(*)
            FROM place_accessibility pa
            WHERE pa.user_id = :userId
        """.trimIndent())
        query.setParameter("userId", userId)
        return (query.singleResult as BigInteger).toInt()
    }

    override fun hasAccessibilityNotRegisteredPlaceInBuilding(buildingId: String): Boolean {
        val em = EntityManagerHolder.get()!!
        val query = em.createQuery("""
            SELECT count(p.id) > 0
            FROM Place p
            INNER JOIN Building b ON b.id = :buildingId AND b.id = p.building.id
            LEFT OUTER JOIN PlaceAccessibility pa ON p.id = pa.placeId
            WHERE pa.id IS NULL
        """.trimIndent())
        query.setParameter("buildingId", buildingId)
        return query.singleResult as Boolean
    }

    override fun countAll(): Int {
        val em = EntityManagerHolder.get()!!
        val query = em.createNativeQuery("""
            SELECT COUNT(*)
            FROM place_accessibility pa
        """.trimIndent())
        return (query.singleResult as BigInteger).toInt()
    }

    override fun listConquerRankingEntries(): List<Pair<String, Int>> {
        val em = EntityManagerHolder.get()!!
        val query = em.createNativeQuery("""
            SELECT user_id, COUNT(*) as count
            FROM place_accessibility pa
            GROUP BY user_id
            ORDER BY count DESC
        """.trimIndent())
        return query.resultList.map {
            val row = it as Array<Object>
            (row[0] as String) to (row[1] as BigInteger).toInt()
        }
    }
}
