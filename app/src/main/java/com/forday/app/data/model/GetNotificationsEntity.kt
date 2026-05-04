package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.GetNotificationsDomain
import com.forday.app.domain.model.NotificationItemDomain
import com.forday.app.domain.model.NotificationTypeDomain
import com.forday.app.domain.model.PushInfoDomain
import com.forday.app.domain.model.ReactionAlarmDomain
import com.forday.app.domain.model.CommentAlarmDomain

data class GetNotificationsEntity(
    val pushInfo: PushInfoEntity,
    val notificationList: List<NotificationItemEntity>,
    val hasNext: Boolean,
    val lastNotificationId: Int?
) : DataMapper<GetNotificationsDomain> {
    override fun toDomain(): GetNotificationsDomain = GetNotificationsDomain(
        pushInfo = pushInfo.toDomain(),
        notificationList = notificationList.map { it.toDomain() },
        hasNext = hasNext,
        lastNotificationId = lastNotificationId
    )
}

data class PushInfoEntity(
    val pushEnabled: Boolean,
    val message: String?
) : DataMapper<PushInfoDomain> {
    override fun toDomain(): PushInfoDomain = PushInfoDomain(
        pushEnabled = pushEnabled,
        message = message
    )
}

data class NotificationItemEntity(
    val notificationId: Int,
    val imageUrl: String?,
    val message: String,
    val type: String,
    val reactionAlarm: ReactionAlarmEntity?,
    val commentAlarm: CommentAlarmEntity?,
    val isUnread: Boolean,
    val senderProfileUrl: String?,
    val createdAt: String
) : DataMapper<NotificationItemDomain> {
    override fun toDomain(): NotificationItemDomain = NotificationItemDomain(
        notificationId = notificationId,
        imageUrl = imageUrl,
        message = message,
        type = NotificationTypeDomain.from(type),
        reactionAlarm = reactionAlarm?.toDomain(),
        commentAlarm = commentAlarm?.toDomain(),
        isUnread = isUnread,
        senderProfileUrl = senderProfileUrl,
        createdAt = createdAt
    )
}

data class ReactionAlarmEntity(
    val reactionType: String,
    val recordId: Int
) : DataMapper<ReactionAlarmDomain> {
    override fun toDomain(): ReactionAlarmDomain = ReactionAlarmDomain(
        reactionType = reactionType,
        recordId = recordId
    )
}

data class CommentAlarmEntity(
    val recordId: Int,
    val commentId: Int,
    val commentContent: String
) : DataMapper<CommentAlarmDomain> {
    override fun toDomain(): CommentAlarmDomain = CommentAlarmDomain(
        recordId = recordId,
        commentId = commentId,
        commentContent = commentContent
    )
}
