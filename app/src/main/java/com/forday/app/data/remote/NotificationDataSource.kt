package com.forday.app.data.remote

import com.forday.app.data.model.FcmTokenUpdateEntity
import com.forday.app.data.model.GetNotificationsEntity
import com.forday.app.data.model.NotificationToggleEntity
import com.forday.app.data.model.NotificationToggleStatusEntity

interface NotificationDataSource {
    suspend fun toggleNotification(active: Boolean, toggleType: String): NotificationToggleEntity
    suspend fun getNotificationToggleStatus(): NotificationToggleStatusEntity
    suspend fun updateFcmToken(fcmToken: String, deviceId: String): FcmTokenUpdateEntity
    suspend fun getNotifications(
        filterType: String,
        lastNotificationId: Int?,
        pageSize: Int
    ): GetNotificationsEntity
}
