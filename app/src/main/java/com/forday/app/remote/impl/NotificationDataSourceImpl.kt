package com.forday.app.remote.impl

import com.forday.app.data.model.FcmTokenUpdateEntity
import com.forday.app.data.model.GetNotificationsEntity
import com.forday.app.data.model.NotificationToggleEntity
import com.forday.app.data.model.NotificationToggleStatusEntity
import com.forday.app.data.remote.NotificationDataSource
import com.forday.app.remote.api.service.NotificationApi
import com.forday.app.remote.model.request.FcmTokenUpdateRequest
import com.forday.app.remote.model.request.NotificationToggleRequest
import javax.inject.Inject

class NotificationDataSourceImpl @Inject constructor(
    private val notificationApi: NotificationApi
) : NotificationDataSource {

    override suspend fun toggleNotification(active: Boolean, toggleType: String): NotificationToggleEntity =
        notificationApi.toggleNotification(NotificationToggleRequest(active, toggleType)).toData()

    override suspend fun getNotificationToggleStatus(): NotificationToggleStatusEntity =
        notificationApi.getNotificationToggleStatus().toData()

    override suspend fun updateFcmToken(fcmToken: String, deviceId: String): FcmTokenUpdateEntity =
        notificationApi.updateFcmToken(FcmTokenUpdateRequest(fcmToken, deviceId)).toData()

    override suspend fun getNotifications(
        filterType: String,
        lastNotificationId: Int?,
        pageSize: Int
    ): GetNotificationsEntity =
        notificationApi.getNotifications(filterType, lastNotificationId, pageSize).toData()
}
