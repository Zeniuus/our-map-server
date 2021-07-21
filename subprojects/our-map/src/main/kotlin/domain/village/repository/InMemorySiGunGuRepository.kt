package domain.village.repository

import domain.village.entity.SiGunGu

class InMemorySiGunGuRepository : SiGunGuRepository {
    // TODO: TSV 파싱해서 가져오기
    private val siGunGus = listOf(
        SiGunGu(id = "1", name = "성남시 분당구", sido = "경기도")
    )

    override fun findById(id: String): SiGunGu {
        return siGunGus.find { it.id == id }!!
    }
}
