package infra.persistence.repository

import domain.place.entity.Building
import domain.place.repository.BuildingRepository

class JpaBuildingRepository :
    JpaEntityRepositoryBase<Building, String>(Building::class.java),
    BuildingRepository
