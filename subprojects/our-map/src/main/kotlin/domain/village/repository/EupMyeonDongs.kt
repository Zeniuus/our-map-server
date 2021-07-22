package domain.village.repository

import domain.village.entity.EupMyeonDong
import domain.village.entity.SiGunGu
import util.HashUtil
import util.TextResourceReader

object EupMyeonDongs {
    val eupMyeonDongs = run {
        val lines = TextResourceReader.read("eupMyeonDong.tsv").split("\n")
        val eupMyeonDongs = lines.map { line ->
            val (siDo, siGunGu, eupMyeonDong) = line.split("\t")
            EupMyeonDong(
                id = HashUtil.getHash("$siDo $siGunGu $eupMyeonDong".toByteArray()),
                name = eupMyeonDong,
                siGunGu = SiGunGu(
                    id = HashUtil.getHash("$siDo $siGunGu".toByteArray()),
                    name = siGunGu,
                    sido = siDo,
                ),
            )
        }
        eupMyeonDongs.forEach { eupMyeonDong ->
            require(eupMyeonDongs.count { it.id == eupMyeonDong.id } == 1)
        }
        eupMyeonDongs
    }

    val siGunGus = eupMyeonDongs.map { it.siGunGu }.toSet().toList()
}
