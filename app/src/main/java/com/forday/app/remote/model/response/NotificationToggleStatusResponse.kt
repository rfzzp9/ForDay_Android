package com.forday.app.remote.model.response

import com.forday.app.data.model.NotificationToggleStatusEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

data class NotificationToggleStatusResponse(
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("data")
    val data: NotificationToggleStatusData
) : RemoteMapper<NotificationToggleStatusEntity> {
    override fun toData(): NotificationToggleStatusEntity = NotificationToggleStatusEntity(
        appPushEnabled = data.appPushEnabled,
        recordPushEnabled = data.recordPushEnabled
    )
}

data class NotificationToggleStatusData(
    @SerializedName("appPushEnabled")
    val appPushEnabled: Boolean,
    @SerializedName("recordPushEnabled")
    val recordPushEnabled: Boolean
)
