package com.forday.app.remote.api.service

import com.forday.app.remote.model.request.FcmTokenUpdateRequest
import com.forday.app.remote.model.request.NotificationToggleRequest
import com.forday.app.remote.model.response.FcmTokenUpdateResponse
import com.forday.app.remote.model.response.GetNotificationsResponse
import com.forday.app.remote.model.response.NotificationToggleResponse
import com.forday.app.remote.model.response.NotificationToggleStatusResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Query

interface NotificationApi {

    @PATCH("/api/notifications/toggle")
    suspend fun toggleNotification(
        @Body request: NotificationToggleRequest
    ): NotificationToggleResponse

    @GET("/api/notifications/toggle")
    suspend fun getNotificationToggleStatus(): NotificationToggleStatusResponse

    @PATCH("/app/fcm-token")
    suspend fun updateFcmToken(
        @Body request: FcmTokenUpdateRequest
    ): FcmTokenUpdateResponse

    @GET("/api/notifications")
    suspend fun getNotifications(
        @Query("filterType") filterType: String,
        @Query("lastNotificationId") lastNotificationId: Int?,
        @Query("pageSize") pageSize: Int
    ): GetNotificationsResponse
}
