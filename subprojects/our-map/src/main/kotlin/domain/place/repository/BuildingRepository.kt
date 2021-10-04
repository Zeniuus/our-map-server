package domain.place.repository

import domain.EntityRepository
import domain.place.entity.Building
import domain.village.entity.EupMyeonDong

interface BuildingRepository : EntityRepository<Building, String> {
    fun countByEupMyeonDong(eupMyeonDong: EupMyeonDong): Int
    fun findByIdIn(ids: Collection<String>): List<Building>
}
