package com.forday.app.presentation.allsettings

data class SettingsUiState(
    val isLoggedOut: Boolean = false,
    val error: String? = null,
    val isAccountCancelled: Boolean = false
)
