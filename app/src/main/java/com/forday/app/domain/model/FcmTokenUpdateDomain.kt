package com.forday.app.domain.model

data class FcmTokenUpdateDomain(
    val message: String?,
    val fcmToken: String,
    val deviceId: String
)
