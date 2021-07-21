package domain.village.repository

import domain.EntityRepository
import domain.village.entity.Village

interface VillageRepository : EntityRepository<Village, String>
