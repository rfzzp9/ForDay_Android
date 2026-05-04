package com.forday.app.remote.model.response

import com.forday.app.data.model.NotificationToggleEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

data class NotificationToggleResponse(
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("data")
    val data: NotificationToggleData
) : RemoteMapper<NotificationToggleEntity> {
    override fun toData(): NotificationToggleEntity = NotificationToggleEntity(
        message = data.message,
        active = data.active,
        toggleType = data.toggleType
    )
}

data class NotificationToggleData(
    @SerializedName("message")
    val message: String?,
    @SerializedName("active")
    val active: Boolean,
    @SerializedName("toggleType")
    val toggleType: String
)
