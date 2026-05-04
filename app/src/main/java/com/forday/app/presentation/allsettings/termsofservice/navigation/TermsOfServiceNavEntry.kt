package com.forday.app.presentation.allsettings.termsofservice.navigation

import androidx.compose.ui.graphics.Color
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.forday.app.presentation.allsettings.termsofservice.screen.TermsOfServiceScreen
import com.forday.app.presentation.main.Navigator
import com.forday.app.presentation.main.nonTabEntry

fun EntryProviderScope<NavKey>.termsOfServiceNavEntry(
    navigator: Navigator,
) {
    nonTabEntry<TermsOfService>(backgroundColor = Color(0xFFF9F9F9)) {
        TermsOfServiceScreen(onCloseClick = { navigator.goBack() })
    }
}
