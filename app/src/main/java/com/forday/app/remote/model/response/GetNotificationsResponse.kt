package com.forday.app.remote.model.response

import com.forday.app.data.model.GetNotificationsEntity
import com.forday.app.data.model.NotificationItemEntity
import com.forday.app.data.model.PushInfoEntity
import com.forday.app.data.model.ReactionAlarmEntity
import com.forday.app.data.model.CommentAlarmEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

data class GetNotificationsResponse(
    @SerializedName("status") val status: Int? = null,
    @SerializedName("success") val success: Boolean? = null,
    @SerializedName("data") val data: NotificationsDataResponse? = null
) : RemoteMapper<GetNotificationsEntity> {
    override fun toData(): GetNotificationsEntity = GetNotificationsEntity(
        pushInfo = data?.pushInfo?.toData() ?: PushInfoEntity(pushEnabled = false, message = null),
        notificationList = data?.notificationList?.map { it.toData() } ?: emptyList(),
        hasNext = data?.hasNext ?: false,
        lastNotificationId = data?.lastNotificationId?.toIntOrNull()
    )
}

data class NotificationsDataResponse(
    @SerializedName("pushInfo") val pushInfo: PushInfoResponse? = null,
    @SerializedName("notificationList") val notificationList: List<NotificationItemResponse>? = null,
    @SerializedName("hasNext") val hasNext: Boolean? = null,
    @SerializedName("lastNotificationId") val lastNotificationId: String? = null
)

data class PushInfoResponse(
    @SerializedName("pushEnabled") val pushEnabled: Boolean? = null,
    @SerializedName("message") val message: String? = null
) : RemoteMapper<PushInfoEntity> {
    override fun toData(): PushInfoEntity = PushInfoEntity(
        pushEnabled = pushEnabled ?: false,
        message = message
    )
}

data class NotificationItemResponse(
    @SerializedName("notificationId") val notificationId: Int? = null,
    @SerializedName("imageUrl") val imageUrl: String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("reactionAlram") val reactionAlram: ReactionAlarmResponse? = null,
    @SerializedName("commentAlram") val commentAlram: CommentAlarmResponse? = null,
    @SerializedName("read") val read: Boolean? = null,
    @SerializedName("senderProfileUrl") val senderProfileUrl: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null
) : RemoteMapper<NotificationItemEntity> {
    override fun toData(): NotificationItemEntity = NotificationItemEntity(
        notificationId = notificationId ?: 0,
        imageUrl = imageUrl,
        message = message ?: "",
        type = type ?: "",
        reactionAlarm = reactionAlram?.toData(),
        commentAlarm = commentAlram?.toData(),
        isUnread = !(read ?: true),
        senderProfileUrl = senderProfileUrl,
        createdAt = createdAt ?: ""
    )
}

data class ReactionAlarmResponse(
    @SerializedName("reactionType") val reactionType: String? = null,
    @SerializedName("recordId") val recordId: Int? = null
) : RemoteMapper<ReactionAlarmEntity> {
    override fun toData(): ReactionAlarmEntity = ReactionAlarmEntity(
        reactionType = reactionType ?: "",
        recordId = recordId ?: 0
    )
}

data class CommentAlarmResponse(
    @SerializedName("recordId") val recordId: Int? = null,
    @SerializedName("commentId") val commentId: Int? = null,
    @SerializedName("commentContent") val commentContent: String? = null
) : RemoteMapper<CommentAlarmEntity> {
    override fun toData(): CommentAlarmEntity = CommentAlarmEntity(
        recordId = recordId ?: 0,
        commentId = commentId ?: 0,
        commentContent = commentContent ?: ""
    )
}
