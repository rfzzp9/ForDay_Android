package com.forday.app.remote.model.response

import com.forday.app.data.model.FcmTokenUpdateEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

data class FcmTokenUpdateResponse(
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("data")
    val data: FcmTokenUpdateData
) : RemoteMapper<FcmTokenUpdateEntity> {
    override fun toData(): FcmTokenUpdateEntity = FcmTokenUpdateEntity(
        message = data.message,
        fcmToken = data.fcmToken,
        deviceId = data.deviceId
    )
}

data class FcmTokenUpdateData(
    @SerializedName("message")
    val message: String?,
    @SerializedName("fcmToken")
    val fcmToken: String,
    @SerializedName("deviceId")
    val deviceId: String
)
