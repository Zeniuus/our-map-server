package infra.persistence.repository

import domain.place.entity.Place
import domain.place.repository.PlaceRepository
import domain.village.entity.EupMyeonDong
import infra.persistence.transaction.EntityManagerHolder

class JpaPlaceRepository :
    JpaEntityRepositoryBase<Place, String>(Place::class.java),
    PlaceRepository {
    override fun findByNameContains(searchText: String): List<Place> {
        val em = EntityManagerHolder.get()!!
        val query = em.createQuery("""
            SELECT p
            FROM Place p
            WHERE p.name LIKE :searchText
        """.trimIndent(), Place::class.java)
        query.setParameter("searchText", "%$searchText%")
        return query.resultList
    }

    override fun countByEupMyeonDong(eupMyeonDong: EupMyeonDong): Int {
        val em = EntityManagerHolder.get()!!
        val query = em.createQuery("""
            SELECT COUNT(p.id)
            FROM Place p
            WHERE p.eupMyeonDongId = :eupMyeonDongId
        """.trimIndent())
        query.setParameter("eupMyeonDongId", eupMyeonDong.id)
        return (query.singleResult as java.lang.Long).toInt()
    }
}
