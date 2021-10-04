package infra.persistence.repository

import domain.place.entity.Building
import domain.place.repository.BuildingRepository
import domain.village.entity.EupMyeonDong
import infra.persistence.transaction.EntityManagerHolder

class JpaBuildingRepository :
    JpaEntityRepositoryBase<Building, String>(Building::class.java),
    BuildingRepository {
    override fun countByEupMyeonDong(eupMyeonDong: EupMyeonDong): Int {
        val em = EntityManagerHolder.get()!!
        val query = em.createQuery("""
            SELECT COUNT(b.id)
            FROM Building b
            WHERE b.eupMyeonDongId = :eupMyeonDongId
        """.trimIndent())
        query.setParameter("eupMyeonDongId", eupMyeonDong.id)
        return (query.singleResult as java.lang.Long).toInt()
    }

    override fun findByIdIn(ids: Collection<String>): List<Building> {
        val em = EntityManagerHolder.get()!!
        val query = em.createQuery("""
            SELECT b
            FROM Building b
        """.trimIndent(), Building::class.java)
        return query.resultList
    }
}
