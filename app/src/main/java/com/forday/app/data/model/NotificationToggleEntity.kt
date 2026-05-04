package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.NotificationToggleDomain

data class NotificationToggleEntity(
    val message: String?,
    val active: Boolean,
    val toggleType: String
) : DataMapper<NotificationToggleDomain> {
    override fun toDomain(): NotificationToggleDomain = NotificationToggleDomain(
        message = message,
        active = active,
        toggleType = toggleType
    )
}
