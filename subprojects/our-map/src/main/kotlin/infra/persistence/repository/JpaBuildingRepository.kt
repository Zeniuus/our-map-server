package infra.persistence.repository

import domain.place.entity.Building
import domain.place.repository.BuildingRepository

class JpaBuildingRepository(
    entityManagerHolder: GlobalEntityManagerHolder,
) : JpaEntityRepositoryBase<Building, String>(Building::class.java, entityManagerHolder),
    BuildingRepository
