package infra.persistence.repository

import domain.place.entity.Place
import domain.place.repository.PlaceRepository
import domain.village.entity.EupMyeonDong
import infra.persistence.transaction.EntityManagerHolder
import org.hibernate.Session

class JpaPlaceRepository :
    JpaEntityRepositoryBase<Place, String>(Place::class.java),
    PlaceRepository {
    override fun findByNameContains(searchTextRegex: String): List<Place> {
        val em = EntityManagerHolder.get()!!
        val session = em.unwrap(Session::class.java)
        val query = session.createNativeQuery("""
            SELECT *
            FROM place p
            LEFT OUTER JOIN building b ON b.id = p.building_id
            WHERE REPLACE(p.name, ' ', '') REGEXP :searchTextRegex
        """.trimIndent())
            .addEntity("p", Place::class.java)
            .addJoin("b", "p.building")
            .setParameter("searchTextRegex", searchTextRegex)
        val result = query.list()
        return result.map {
            (it as Array<Object>)[0] as Place
        }
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
