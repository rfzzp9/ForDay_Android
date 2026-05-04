package com.forday.app.domain.repository

import com.forday.app.domain.model.FcmTokenUpdateDomain
import com.forday.app.domain.model.GetNotificationsDomain
import com.forday.app.domain.model.NotificationToggleDomain
import com.forday.app.domain.model.NotificationToggleStatusDomain

interface NotificationRepository {
    suspend fun toggleNotification(active: Boolean, toggleType: String): NotificationToggleDomain
    suspend fun getNotificationToggleStatus(): NotificationToggleStatusDomain
    suspend fun updateFcmToken(): FcmTokenUpdateDomain
    suspend fun getNotifications(
        filterType: String,
        lastNotificationId: Int?,
        pageSize: Int
    ): GetNotificationsDomain
}
