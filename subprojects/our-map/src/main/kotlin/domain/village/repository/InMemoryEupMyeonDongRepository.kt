package domain.village.repository

import domain.village.entity.EupMyeonDong

class InMemoryEupMyeonDongRepository : EupMyeonDongRepository {
    private val eupMyeonDongs = EupMyeonDongs.eupMyeonDongs

    override fun findById(id: String): EupMyeonDong {
        return eupMyeonDongs.find { it.id == id }!!
    }

    override fun listAll() = eupMyeonDongs
}
