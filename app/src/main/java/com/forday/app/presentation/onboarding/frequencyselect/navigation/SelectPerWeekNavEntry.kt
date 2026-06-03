package com.forday.app.presentation.onboarding.frequencyselect.navigation

import androidx.compose.ui.graphics.Color
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.forday.app.presentation.main.Navigator
import com.forday.app.presentation.main.nonTabEntry
import com.forday.app.presentation.onboarding.OnboardingFlowViewModel
import com.forday.app.presentation.onboarding.frequencyselect.SelectFrequencyRoute
import com.forday.app.presentation.onboarding.periodselect.navigation.SelectPeriod
import com.forday.app.presentation.onboarding.timeselect.ScreenMode

fun EntryProviderScope<NavKey>.selectPerWeekNavEntry(
    navigator: Navigator,
    onboardingFlowViewModel: OnboardingFlowViewModel,
) {
    nonTabEntry<SelectPerWeek>(backgroundColor = Color(0xFFF9F9F9)) { backStackEntry ->
        SelectFrequencyRoute(
            params = backStackEntry.params,
            onNext = {
                when (backStackEntry.mode) {
                    ScreenMode.ONBOARDING -> navigator.navigate(SelectPeriod(mode = ScreenMode.ONBOARDING))
                    ScreenMode.ADD_FROM_HOME -> navigator.navigate(SelectPeriod(mode = ScreenMode.ADD_FROM_HOME))
                    ScreenMode.DEFAULT -> navigator.goBack()
                }
            },
            onBack = { navigator.goBack() },
            viewModel = onboardingFlowViewModel,
            mode = backStackEntry.mode
        )
    }
}
