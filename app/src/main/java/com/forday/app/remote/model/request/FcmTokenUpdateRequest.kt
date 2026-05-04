package com.forday.app.remote.model.request

import com.google.gson.annotations.SerializedName

data class FcmTokenUpdateRequest(
    @SerializedName("fcmToken")
    val fcmToken: String,
    @SerializedName("deviceId")
    val deviceId: String
)
