package converter

import domain.accessibility.entity.BuildingAccessibilityComment
import domain.user.entity.User
import ourMap.protocol.Common
import ourMap.protocol.Model

object BuildingAccessibilityCommentConverter {
    fun toProto(comment: BuildingAccessibilityComment, userCache: Map<String, User>): Model.BuildingAccessibilityComment {
        return Model.BuildingAccessibilityComment.newBuilder()
            .setId(comment.id)
            .setBuildingId(comment.buildingId)
            .also {
                val commentedUser = comment.userId?.let { userCache[it] }
                if (commentedUser != null) {
                    it.user = UserConverter.toProto(commentedUser)
                }
            }
            .setComment(comment.comment)
            .setCreatedAt(Common.Timestamp.newBuilder().setValue(comment.createdAt.toEpochMilli()))
            .build()
    }
}
