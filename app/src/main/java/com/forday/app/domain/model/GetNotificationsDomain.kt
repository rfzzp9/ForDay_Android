package com.forday.app.domain.model

data class GetNotificationsDomain(
    val pushInfo: PushInfoDomain,
    val notificationList: List<NotificationItemDomain>,
    val hasNext: Boolean,
    val lastNotificationId: Int?
)

data class PushInfoDomain(
    val pushEnabled: Boolean,
    val message: String?
)

data class NotificationItemDomain(
    val notificationId: Int,
    val imageUrl: String?,
    val message: String,
    val type: NotificationTypeDomain,
    val reactionAlarm: ReactionAlarmDomain?,
    val commentAlarm: CommentAlarmDomain?,
    val isUnread: Boolean,
    val senderProfileUrl: String?,
    val createdAt: String
)

enum class NotificationTypeDomain {
    RECORD_REACTION,
    RECORD_COMMENT,
    UNKNOWN;

    companion object {
        fun from(value: String): NotificationTypeDomain = entries.find { it.name == value } ?: UNKNOWN
    }
}

data class ReactionAlarmDomain(
    val reactionType: String,
    val recordId: Int
)

data class CommentAlarmDomain(
    val recordId: Int,
    val commentId: Int,
    val commentContent: String
)
