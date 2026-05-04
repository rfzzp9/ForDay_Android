package com.forday.app.presentation.onboarding.showpobbies.navigation

import androidx.activity.compose.BackHandler
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.forday.app.presentation.main.Navigator
import com.forday.app.presentation.main.nonTabEntry
import com.forday.app.presentation.onboarding.showpobbies.OnboardingSuccessScreen

fun EntryProviderScope<NavKey>.onboardingSuccessNavEntry(
    navigator: Navigator,
) {
    nonTabEntry<OnboardingSuccess> {
        BackHandler(enabled = true) { }
        OnboardingSuccessScreen(
            onNext = { navigator.navigate(ShowPobbies) },
            onDirectHome = { navigator.resetTo(com.forday.app.presentation.home.navigation.Home) },
        )
    }
}
