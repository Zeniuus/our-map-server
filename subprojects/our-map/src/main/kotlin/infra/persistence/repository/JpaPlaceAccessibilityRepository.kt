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

    override fun countAll(): Int {
        val em = EntityManagerHolder.get()!!
        val query = em.createNativeQuery("""
            SELECT COUNT(*)
            FROM place_accessibility pa
        """.trimIndent())
        return (query.singleResult as BigInteger).toInt()
    }
}
