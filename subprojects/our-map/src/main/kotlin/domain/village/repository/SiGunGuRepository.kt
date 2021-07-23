package domain.village.repository

import domain.village.entity.SiGunGu

interface SiGunGuRepository {
    fun findById(id: String): SiGunGu
    fun listAll(): List<SiGunGu>
}
