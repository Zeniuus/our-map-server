package domain.village.repository

import domain.village.entity.EupMyeonDong

interface EupMyeonDongRepository {
    fun findById(id: String): EupMyeonDong
    fun listAll(): List<EupMyeonDong>
}
