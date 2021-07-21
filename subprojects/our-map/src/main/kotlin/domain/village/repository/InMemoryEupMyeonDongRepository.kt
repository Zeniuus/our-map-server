package domain.village.repository

import domain.village.entity.EupMyeonDong
import domain.village.entity.SiGunGu

class InMemoryEupMyeonDongRepository : EupMyeonDongRepository {
    // TODO: TSV 파싱해서 가져오기
    private val eupMyeonDongs = listOf(
        EupMyeonDong(
            id = "1",
            name = "이매동",
            siGunGu = SiGunGu(
                id = "1",
                name = "성남시 분당구",
                sido = "경기도",
            ),
        )
    )

    override fun findById(id: String): EupMyeonDong {
        return eupMyeonDongs.find { it.id == id }!!
    }
}
