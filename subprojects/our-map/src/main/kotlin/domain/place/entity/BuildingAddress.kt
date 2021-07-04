package domain.place.entity

import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.Transient

@Embeddable
data class BuildingAddress(
    @Column(length = 8, nullable = false)
    val siDo: String,
    @Column(length = 8, nullable = false)
    val siGunGu: String,
    @Column(length = 8, nullable = false)
    val eupMyeonDong: String,
    @Column(length = 8, nullable = false)
    val li: String,
    @Column(length = 16, nullable = false)
    val roadName: String,
    @Column(length = 8, nullable = false)
    val mainBuildingNumber: String,
    @Column(length = 4, nullable = false)
    val subBuildingNumber: String
) {
    @Transient
    override fun toString(): String {
        // refs: https://www.juso.go.kr/CommonPageLink.do?link=/street/GuideBook
        return buildString {
            append("$siDo $siGunGu ")
            if (!eupMyeonDong.endsWith("동")) {
                append("$eupMyeonDong ")
            }
            append("$roadName ")
            append(mainBuildingNumber)
            if (subBuildingNumber.isNotBlank() && subBuildingNumber != "0") {
                append("-$subBuildingNumber")
            }
        }
    }
}
