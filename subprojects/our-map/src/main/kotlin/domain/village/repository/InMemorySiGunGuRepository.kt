package domain.village.repository

import domain.village.entity.SiGunGu

class InMemorySiGunGuRepository : SiGunGuRepository {
    private val siGunGus = EupMyeonDongs.siGunGus

    override fun findById(id: String): SiGunGu {
        return siGunGus.find { it.id == id }!!
    }
}
