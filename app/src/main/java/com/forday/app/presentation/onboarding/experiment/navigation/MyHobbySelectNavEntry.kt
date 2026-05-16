package com.forday.app.presentation.onboarding.experiment.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.forday.app.presentation.main.Navigator
import com.forday.app.presentation.main.nonTabEntry
import com.forday.app.presentation.onboarding.OnboardingFlowViewModel
import com.forday.app.presentation.onboarding.experiment.MyHobbySelectRoute
import com.forday.app.presentation.onboarding.showpobbies.navigation.OnboardingSuccess

fun EntryProviderScope<NavKey>.myHobbySelectNavEntry(
    navigator: Navigator,
    onboardingFlowViewModel: OnboardingFlowViewModel,
) {
    nonTabEntry<MyHobbySelect> { backStackEntry ->
        MyHobbySelectRoute(
            userName = backStackEntry.userName,
            onNext = { navigator.navigate(OnboardingSuccess(goHomeAfterShowPobbies = true)) },
            viewModel = onboardingFlowViewModel,
        )
    }
}
