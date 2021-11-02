package domain.place.repository

import domain.EntityRepository
import domain.place.entity.Place
import domain.village.entity.EupMyeonDong

interface PlaceRepository : EntityRepository<Place, String> {
    fun findByNameContains(searchTextRegex: String): List<Place>
    /**
     * fetch join:
     * - building
     */
    fun findByBuildingId(buildingId: String): List<Place>
    fun countByEupMyeonDong(eupMyeonDong: EupMyeonDong): Int
}
