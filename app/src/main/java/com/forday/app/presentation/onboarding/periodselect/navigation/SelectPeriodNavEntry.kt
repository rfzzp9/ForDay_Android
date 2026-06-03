package com.forday.app.presentation.onboarding.periodselect.navigation

import androidx.compose.ui.graphics.Color
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.forday.app.presentation.main.Navigator
import com.forday.app.presentation.main.nonTabEntry
import com.forday.app.presentation.onboarding.OnboardingFlowViewModel
import com.forday.app.presentation.onboarding.periodselect.SelectJourneyDaysRoute
import com.forday.app.presentation.onboarding.showpobbies.navigation.OnboardingSuccess
import com.forday.app.presentation.onboarding.timeselect.ScreenMode
import com.forday.app.presentation.home.navigation.Home
import timber.log.Timber

fun EntryProviderScope<NavKey>.selectPeriodNavEntry(
    navigator: Navigator,
    onboardingFlowViewModel: OnboardingFlowViewModel,
) {
    nonTabEntry<SelectPeriod>(backgroundColor = Color(0xFFF9F9F9)) { backStackEntry ->
        Timber.e("@@@@@@@@@@@@##@#@#@#@# mode : "+backStackEntry.mode)
        SelectJourneyDaysRoute(
            params = backStackEntry.params,
            mode = backStackEntry.mode,
            onNext = {
                when (backStackEntry.mode) {
                    ScreenMode.ONBOARDING -> navigator.navigate(OnboardingSuccess())
                    ScreenMode.ADD_FROM_HOME -> navigator.popBackStack(5)
                    ScreenMode.DEFAULT -> navigator.goBack()
                }
            },
            onBack = {
                Timber.e("@@@@@@@@@@@@@@ onBack")
                navigator.goBack()
            },
            viewModel = onboardingFlowViewModel,
            goHome = { navigator.resetTo(Home) }
        )
    }
}
