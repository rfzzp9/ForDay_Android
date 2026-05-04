package com.forday.app.presentation.onboarding.swipeintro.navigation

import androidx.compose.ui.graphics.Color
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.forday.app.presentation.main.Navigator
import com.forday.app.presentation.main.nonTabEntry
import com.forday.app.presentation.onboarding.OnboardingViewModel
import com.forday.app.presentation.onboarding.swipeintro.SwipeIntroScreen

fun EntryProviderScope<NavKey>.swipeIntroNavEntry(
    navigator: Navigator,
    onboardingViewModel: OnboardingViewModel,
) {
    nonTabEntry<SwipeIntro>(backgroundColor = Color(0xFFFFF5EE)) {
        SwipeIntroScreen(
            onNavigateToLogin = {
                onboardingViewModel.saveHasSeenIntro()
                navigator.resetTo(com.forday.app.presentation.onboarding.login.navigation.Login)
            }
        )
    }
}
