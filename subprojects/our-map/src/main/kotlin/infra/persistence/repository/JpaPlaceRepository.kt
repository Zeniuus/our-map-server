package infra.persistence.repository

import domain.place.entity.Place
import domain.place.repository.BuildingRepository
import domain.place.repository.PlaceRepository
import domain.village.entity.EupMyeonDong
import infra.persistence.transaction.EntityManagerHolder
import org.hibernate.Hibernate
import org.hibernate.Session

class JpaPlaceRepository(
    private val buildingRepository: BuildingRepository,
) :
    JpaEntityRepositoryBase<Place, String>(Place::class.java),
    PlaceRepository {
    override fun findByNameContains(searchTextRegex: String): List<Place> {
        val em = EntityManagerHolder.get()!!
        val session = em.unwrap(Session::class.java)
        val query = session.createNativeQuery("""
            SELECT *
            FROM place p
            WHERE REPLACE(p.name, ' ', '') REGEXP :searchTextRegex
        """.trimIndent(), Place::class.java)
        query.setParameter("searchTextRegex", searchTextRegex)
        val places = query.resultList.map { it as Place }
        // 1+N 문제 방지를 위해 buildings를 session에 미리 로딩하고 Hibernate.initialize()를 호출한다.
        buildingRepository.findByIdIn(places.map { it.building.id })
        places.forEach {
            Hibernate.initialize(it.building)
        }
        return places
    }

    override fun findByBuildingId(buildingId: String): List<Place> {
        val em = EntityManagerHolder.get()!!
        val query = em.createQuery("""
            SELECT p
            FROM Place p
            JOIN FETCH p.building b
            WHERE p.building.id = :buildingId
        """.trimIndent(), Place::class.java)
        query.setParameter("buildingId", buildingId)
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
