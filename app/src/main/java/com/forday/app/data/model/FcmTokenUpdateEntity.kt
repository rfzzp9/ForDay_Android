package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.FcmTokenUpdateDomain

data class FcmTokenUpdateEntity(
    val message: String?,
    val fcmToken: String,
    val deviceId: String
) : DataMapper<FcmTokenUpdateDomain> {
    override fun toDomain(): FcmTokenUpdateDomain = FcmTokenUpdateDomain(
        message = message,
        fcmToken = fcmToken,
        deviceId = deviceId
    )
}
