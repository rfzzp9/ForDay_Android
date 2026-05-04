package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.NotificationToggleStatusDomain

data class NotificationToggleStatusEntity(
    val appPushEnabled: Boolean,
    val recordPushEnabled: Boolean
) : DataMapper<NotificationToggleStatusDomain> {
    override fun toDomain(): NotificationToggleStatusDomain = NotificationToggleStatusDomain(
        appPushEnabled = appPushEnabled,
        recordPushEnabled = recordPushEnabled
    )
}
