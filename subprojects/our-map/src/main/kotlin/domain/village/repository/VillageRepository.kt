package domain.village.repository

import domain.EntityRepository
import domain.village.entity.EupMyeonDong
import domain.village.entity.Village

interface VillageRepository : EntityRepository<Village, String> {
    fun listAll(): List<Village>
    fun findByEupMyeonDong(eupMyeonDong: EupMyeonDong): Village?
}
