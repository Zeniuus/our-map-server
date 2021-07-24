package domain.village.repository

import domain.village.entity.EupMyeonDong
import domain.village.entity.SiGunGu
import util.Hashing
import util.TextResourceReader

object EupMyeonDongs {
    val eupMyeonDongs = run {
        val lines = TextResourceReader.read("eupMyeonDong.tsv").split("\n")
        val eupMyeonDongs = lines.map { line ->
            val (siDo, siGunGu, eupMyeonDong) = line.split("\t")
            EupMyeonDong(
                id = Hashing.getHash("$siDo $siGunGu $eupMyeonDong".toByteArray(), length = 36),
                name = eupMyeonDong,
                siGunGu = SiGunGu(
                    id = Hashing.getHash("$siDo $siGunGu".toByteArray(), length = 36),
                    name = siGunGu,
                    sido = siDo,
                ),
            )
        }
        require(eupMyeonDongs.size == eupMyeonDongs.map { it.id }.toSet().size)
        eupMyeonDongs
    }

    val siGunGus = eupMyeonDongs.map { it.siGunGu }.toSet().toList()
}
