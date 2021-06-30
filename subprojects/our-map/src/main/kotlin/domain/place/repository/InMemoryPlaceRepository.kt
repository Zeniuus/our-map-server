package domain.place.repository

import domain.InMemoryEntityRepositoryBase
import domain.place.entity.Place

class InMemoryPlaceRepository : InMemoryEntityRepositoryBase<Place, String>(), PlaceRepository {
    override fun findByNameContains(searchText: String): List<Place> {
        return entities.filter { searchText in it.name }
    }
}
