package com.forday.app.presentation.home.hobbysetting.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.forday.app.presentation.home.hobbysetting.HomeHobbySettingRoute
import com.forday.app.presentation.main.Navigator
import com.forday.app.presentation.main.nonTabEntry

fun EntryProviderScope<NavKey>.homeHobbySettingNavEntry(
    navigator: Navigator,
) {
    nonTabEntry<HomeHobbySetting> {
        HomeHobbySettingRoute(
            onBackClick = { navigator.goBack() },
        )
    }
}
