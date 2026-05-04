package com.forday.app.domain.model

data class NotificationToggleDomain(
    val message: String?,
    val active: Boolean,
    val toggleType: String
)
