package com.forday.app.presentation.allsettings.privacypolicy.navigation

import androidx.compose.ui.graphics.Color
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.forday.app.presentation.allsettings.privacypolicy.screen.PrivacyPolicyScreen
import com.forday.app.presentation.main.Navigator
import com.forday.app.presentation.main.nonTabEntry

fun EntryProviderScope<NavKey>.privacyPolicyNavEntry(
    navigator: Navigator,
) {
    nonTabEntry<PrivacyPolicy>(backgroundColor = Color(0xFFF9F9F9)) {
        PrivacyPolicyScreen(onCloseClick = { navigator.goBack() })
    }
}
