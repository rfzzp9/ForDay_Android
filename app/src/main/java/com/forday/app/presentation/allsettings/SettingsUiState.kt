package com.forday.app.presentation.allsettings

data class SettingsUiState(
    val error: String? = null,
    val postLikeNotificationEnabled: Boolean = false,
    val pushNotificationEnabled: Boolean = false,
)
