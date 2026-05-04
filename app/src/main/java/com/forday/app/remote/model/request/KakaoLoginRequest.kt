package com.forday.app.remote.model.request

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
@Keep
data class KakaoLoginRequest(
    @SerializedName("kakaoAccessToken")
    val kakaoAccessToken: String,
    val fcmToken: String,
    val deviceId: String,
    val deviceType: String,
)
