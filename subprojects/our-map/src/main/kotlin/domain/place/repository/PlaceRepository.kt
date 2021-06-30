package domain.place.repository

import domain.EntityRepository
import domain.place.entity.Place

interface PlaceRepository : EntityRepository<Place, String> {
    fun findByNameContains(searchText: String): List<Place>
}
