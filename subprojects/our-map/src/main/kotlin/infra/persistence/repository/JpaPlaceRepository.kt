package infra.persistence.repository

import domain.place.entity.Place
import domain.place.repository.PlaceRepository

class JpaPlaceRepository(
    private val entityManagerHolder: GlobalEntityManagerHolder,
) : JpaEntityRepositoryBase<Place, String>(Place::class.java, entityManagerHolder),
    PlaceRepository {
    override fun findByNameContains(searchText: String): List<Place> {
        val em = entityManagerHolder.get()
        val query = em.createQuery("""
            SELECT p
            FROM Place p
            WHERE p.name LIKE '%:searchText%'
        """.trimIndent(), Place::class.java)
        query.setParameter("searchText", searchText)
        return query.resultList
    }
}
