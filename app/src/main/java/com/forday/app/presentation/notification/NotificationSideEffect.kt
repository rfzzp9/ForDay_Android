package com.forday.app.presentation.notification

sealed interface NotificationSideEffect {
    data object OpenNotificationSettings : NotificationSideEffect
    data class Exception(val throwable: Throwable) : NotificationSideEffect
}
