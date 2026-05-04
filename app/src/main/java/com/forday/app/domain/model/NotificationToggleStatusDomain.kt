package com.forday.app.domain.model

data class NotificationToggleStatusDomain(
    val appPushEnabled: Boolean,
    val recordPushEnabled: Boolean
)
