package com.forday.app.presentation.onboarding.experiment.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.forday.app.presentation.home.navigation.Home
import com.forday.app.presentation.main.Navigator
import com.forday.app.presentation.main.nonTabEntry
import com.forday.app.presentation.onboarding.OnboardingFlowViewModel
import com.forday.app.presentation.onboarding.experiment.MyHobbySelectRoute

fun EntryProviderScope<NavKey>.myHobbySelectNavEntry(
    navigator: Navigator,
    onboardingFlowViewModel: OnboardingFlowViewModel,
) {
    nonTabEntry<MyHobbySelect> {
        MyHobbySelectRoute(
            onNext = { navigator.resetTo(Home) },
            viewModel = onboardingFlowViewModel,
        )
    }
}
