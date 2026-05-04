package com.forday.app.presentation.allsettings.settings.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.forday.app.presentation.allsettings.SettingsViewModel
import com.forday.app.presentation.allsettings.cancelaccount.navigation.CancelAccount
import com.forday.app.presentation.allsettings.privacypolicy.navigation.PrivacyPolicy
import com.forday.app.presentation.allsettings.settings.screen.SettingsScreen
import com.forday.app.presentation.allsettings.termsofservice.navigation.TermsOfService
import com.forday.app.presentation.main.Navigator
import com.forday.app.presentation.main.nonTabEntry

fun EntryProviderScope<NavKey>.settingsNavEntry(
    navigator: Navigator,
    settingsViewModel: SettingsViewModel,
) {
    nonTabEntry<Settings> {
        LaunchedEffect(Unit) { settingsViewModel.loadNotificationStatus() }
        val settingsUiState by settingsViewModel.uiState.collectAsStateWithLifecycle()
        SettingsScreen(
            onBackClick = { navigator.goBack() },
            onTermsOfServiceClick = { navigator.navigate(TermsOfService) },
            onPrivacyPolicyClick = { navigator.navigate(PrivacyPolicy) },
            onCancelAccountClick = { navigator.navigate(CancelAccount) },
            postLikeNotificationEnabled = settingsUiState.postLikeNotificationEnabled,
            pushNotificationEnabled = settingsUiState.pushNotificationEnabled,
            onPostLikeNotificationToggle = { settingsViewModel.togglePostLikeNotification(it) },
            onPushNotificationToggle = { settingsViewModel.togglePushNotification(it) },
            onLogout = { settingsViewModel.logout() },
        )
    }
}
