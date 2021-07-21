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
            SELECT COUNT(*)
            FROM Building b
            WHERE b.eupMyeonDongId = :eupMyeonDongId
        """.trimIndent(), Int::class.java)
        query.setParameter("eupMyeonDongId", eupMyeonDong.id)
        return query.firstResult
    }
}
