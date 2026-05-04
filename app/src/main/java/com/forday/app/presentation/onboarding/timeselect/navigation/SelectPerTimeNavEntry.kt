package com.forday.app.presentation.onboarding.timeselect.navigation

import androidx.compose.ui.graphics.Color
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.forday.app.presentation.main.Navigator
import com.forday.app.presentation.main.nonTabEntry
import com.forday.app.presentation.onboarding.OnboardingFlowViewModel
import com.forday.app.presentation.onboarding.purposeselect.navigation.SelectPurpose
import com.forday.app.presentation.onboarding.timeselect.ScreenMode
import com.forday.app.presentation.onboarding.timeselect.SelectTimeRoute

fun EntryProviderScope<NavKey>.selectPerTimeNavEntry(
    navigator: Navigator,
    onboardingFlowViewModel: OnboardingFlowViewModel,
) {
    nonTabEntry<SelectPerTime>(backgroundColor = Color(0xFFF9F9F9)) { backStackEntry ->
        SelectTimeRoute(
            params = backStackEntry.params,
            onNext = {
                if (backStackEntry.mode == ScreenMode.ONBOARDING) navigator.navigate(SelectPurpose)
                else navigator.goBack()
            },
            onBack = { navigator.goBack() },
            viewModel = onboardingFlowViewModel,
            mode = backStackEntry.mode
        )
    }
}
