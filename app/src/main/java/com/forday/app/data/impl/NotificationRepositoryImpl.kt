package com.forday.app.data.impl

import android.content.Context
import android.provider.Settings
import com.forday.app.core.datastore.UserLocalDataSource
import com.forday.app.data.remote.NotificationDataSource
import com.forday.app.domain.model.FcmTokenUpdateDomain
import com.forday.app.domain.model.GetNotificationsDomain
import com.forday.app.domain.model.NotificationToggleDomain
import com.forday.app.domain.model.NotificationToggleStatusDomain
import com.forday.app.domain.repository.NotificationRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject

internal class NotificationRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationDataSource: NotificationDataSource,
    private val userLocalDataSource: UserLocalDataSource
) : NotificationRepository {

    override suspend fun toggleNotification(active: Boolean, toggleType: String): NotificationToggleDomain =
        notificationDataSource.toggleNotification(active, toggleType).toDomain()

    override suspend fun getNotificationToggleStatus(): NotificationToggleStatusDomain =
        notificationDataSource.getNotificationToggleStatus().toDomain()

    override suspend fun updateFcmToken(): FcmTokenUpdateDomain {
        val fcmToken = userLocalDataSource.getFcmToken().first() ?: ""
        val deviceId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: ""
        val result = notificationDataSource.updateFcmToken(fcmToken, deviceId).toDomain()
        userLocalDataSource.saveFcmToken(result.fcmToken)
        return result
    }

    override suspend fun getNotifications(
        filterType: String,
        lastNotificationId: Int?,
        pageSize: Int
    ): GetNotificationsDomain =
        notificationDataSource.getNotifications(filterType, lastNotificationId, pageSize).toDomain()
}
