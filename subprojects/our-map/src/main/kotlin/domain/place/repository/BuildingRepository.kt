package domain.place.repository

import domain.EntityRepository
import domain.place.entity.Building

interface BuildingRepository : EntityRepository<Building, String>
