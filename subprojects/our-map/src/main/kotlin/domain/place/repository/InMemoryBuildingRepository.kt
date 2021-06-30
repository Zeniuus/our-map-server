package domain.place.repository

import domain.InMemoryEntityRepositoryBase
import domain.place.entity.Building

class InMemoryBuildingRepository : InMemoryEntityRepositoryBase<Building, String>(), BuildingRepository
