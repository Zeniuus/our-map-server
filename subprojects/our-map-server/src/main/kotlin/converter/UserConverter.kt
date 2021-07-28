package converter

import domain.user.entity.User
import ourMap.protocol.Common
import ourMap.protocol.Model

object UserConverter {
    fun toProto(user: User) = Model.User.newBuilder()
        .setId(user.id)
        .setNickname(user.nickname)
        .apply {
            user.instagramId?.let { instagramId = Common.StringValue.newBuilder().setValue(it).build() }
        }
        .build()
}
