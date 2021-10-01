package converter

import domain.accessibility.entity.PlaceAccessibilityComment
import domain.user.entity.User
import ourMap.protocol.Common
import ourMap.protocol.Model

object PlaceAccessibilityCommentConverter {
    fun toProto(comment: PlaceAccessibilityComment, userCache: Map<String, User>): Model.PlaceAccessibilityComment {
        return Model.PlaceAccessibilityComment.newBuilder()
            .setId(comment.id)
            .setPlaceId(comment.placeId)
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
