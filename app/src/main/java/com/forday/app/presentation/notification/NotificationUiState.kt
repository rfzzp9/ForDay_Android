package com.forday.app.presentation.notification

import com.forday.app.domain.model.NotificationItemDomain

data class NotificationUiState(
    val notifications: List<NotificationItemDomain> = emptyList(),
    val showPermissionBanner: Boolean = false,
    val hasNext: Boolean = false,
    val lastNotificationId: Int? = null,
    val isLoading: Boolean = false,
    val isPaginating: Boolean = false,
)
