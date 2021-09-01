package converter

import domain.accessibility.entity.StairInfo
import ourMap.protocol.Model

object StairInfoConverter {
    fun toProto(stairInfo: StairInfo) = when (stairInfo) {
        StairInfo.UNDEFINED -> Model.StairInfo.UNDEFINED
        StairInfo.NONE -> Model.StairInfo.NONE
        StairInfo.ONE -> Model.StairInfo.ONE
        StairInfo.TWO_TO_FIVE -> Model.StairInfo.TWO_TO_FIVE
        StairInfo.OVER_SIX -> Model.StairInfo.OVER_SIX
    }

    fun fromProto(stairInfo: Model.StairInfo) = when (stairInfo) {
        Model.StairInfo.UNDEFINED -> StairInfo.UNDEFINED
        Model.StairInfo.NONE -> StairInfo.NONE
        Model.StairInfo.ONE -> StairInfo.ONE
        Model.StairInfo.TWO_TO_FIVE -> StairInfo.TWO_TO_FIVE
        Model.StairInfo.OVER_SIX -> StairInfo.OVER_SIX
        else -> throw IllegalArgumentException("Invalid stairInfo: $stairInfo")
    }
}
