package application.village

import domain.village.entity.EupMyeonDong
import domain.village.entity.Village

data class VillageDropdownItem(
    val village: Village,
    val eupMyeonDong: EupMyeonDong,
) {
    val villageName = "${eupMyeonDong.siGunGu.name} ${eupMyeonDong.name}"
}
