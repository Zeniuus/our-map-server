package infra.persistence.repository

import domain.village.entity.EupMyeonDong
import domain.village.entity.Village
import domain.village.repository.VillageRepository
import infra.persistence.transaction.EntityManagerHolder

class JpaVillageRepository :
    JpaEntityRepositoryBase<Village, String>(Village::class.java),
    VillageRepository {
    override fun listAll(): List<Village> {
        val em = EntityManagerHolder.get()!!
        val query = em.createQuery("""
            SELECT v
            FROM Village v
        """.trimIndent(), Village::class.java)
        return query.resultList
    }

    override fun findByEupMyeonDong(eupMyeonDong: EupMyeonDong): Village? {
        val em = EntityManagerHolder.get()!!
        val query = em.createQuery("""
            SELECT v
            FROM Village v
            WHERE v.eupMyeonDong = :eupMyeonDong
        """.trimIndent(), Village::class.java)
        query.setParameter("eupMyeonDong", eupMyeonDong)
        return getSingularResultOrThrow(query.resultList)
    }
}
