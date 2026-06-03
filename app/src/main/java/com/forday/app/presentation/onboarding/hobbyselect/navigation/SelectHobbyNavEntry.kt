package com.forday.app.presentation.onboarding.hobbyselect.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.ui.graphics.Color
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.forday.app.presentation.allsettings.SettingsViewModel
import com.forday.app.presentation.main.Navigator
import com.forday.app.presentation.main.nonTabEntry
import com.forday.app.presentation.onboarding.OnboardingFlowViewModel
import com.forday.app.presentation.onboarding.hobbyselect.SelectHobbyRoute
import com.forday.app.presentation.onboarding.timeselect.ScreenMode
import com.forday.app.presentation.onboarding.timeselect.navigation.SelectPerTime

fun EntryProviderScope<NavKey>.selectHobbyNavEntry(
    navigator: Navigator,
    onboardingFlowViewModel: OnboardingFlowViewModel,
    settingsViewModel: SettingsViewModel,
) {
    nonTabEntry<SelectHobby>(backgroundColor = Color(0xFFF9F9F9)) {
        BackHandler(enabled = true) {
            settingsViewModel.logout()
        }
        SelectHobbyRoute(
            onNext = { navigator.navigate(SelectPerTime(mode = ScreenMode.ONBOARDING)) },
            onBack = { settingsViewModel.logout() },
            viewModel = onboardingFlowViewModel
        )
    }
}

fun EntryProviderScope<NavKey>.selectHobbyFromModifyNavEntry(
    navigator: Navigator,
    onboardingFlowViewModel: OnboardingFlowViewModel,
) {
    nonTabEntry<SelectHobbyFromModify>(backgroundColor = Color(0xFFF9F9F9)) {
        SelectHobbyRoute(
            onNext = { navigator.navigate(SelectPerTime(mode = ScreenMode.ADD_FROM_HOME)) },
            onBack = { navigator.goBack() },
            viewModel = onboardingFlowViewModel,
            fromModifyHobbyOrHome = true
        )
    }
}
