package infra.persistence.repository

import domain.place.entity.Place
import domain.place.repository.PlaceRepository
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
}
