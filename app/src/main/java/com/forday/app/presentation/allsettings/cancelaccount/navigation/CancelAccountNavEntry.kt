package com.forday.app.presentation.allsettings.cancelaccount.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.forday.app.presentation.allsettings.SettingsViewModel
import com.forday.app.presentation.allsettings.cancelaccount.screen.CancelAccountScreen
import com.forday.app.presentation.main.Navigator
import com.forday.app.presentation.main.nonTabEntry

fun EntryProviderScope<NavKey>.cancelAccountNavEntry(
    navigator: Navigator,
    settingsViewModel: SettingsViewModel,
) {
    nonTabEntry<CancelAccount> {
        CancelAccountScreen(
            onBackClick = { navigator.goBack() },
            onCancelAccount = { settingsViewModel.cancelAccount() },
        )
    }
}
